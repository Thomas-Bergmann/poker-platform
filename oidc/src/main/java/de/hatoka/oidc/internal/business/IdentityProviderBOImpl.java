package de.hatoka.oidc.internal.business;

import java.text.ParseException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.capi.business.IdentityProviderRef;
import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.oidc.capi.remote.OIDCUserInfo;
import de.hatoka.oidc.internal.persistence.IdentityProviderDao;
import de.hatoka.oidc.internal.persistence.IdentityProviderPO;
import de.hatoka.oidc.internal.persistence.IdentityProviderUserMapDao;
import de.hatoka.oidc.internal.persistence.IdentityProviderUserMapPO;
import de.hatoka.oidc.internal.remote.IdentityProviderClient;
import de.hatoka.oidc.internal.remote.IdentityProviderMetaDataResponse;
import de.hatoka.user.capi.business.UserBO;
import de.hatoka.user.capi.business.UserBORepository;
import de.hatoka.user.capi.business.UserRef;
import jakarta.transaction.Transactional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IdentityProviderBOImpl implements IdentityProviderBO
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdentityProviderDao identityProviderDao;
    @Autowired
    private IdentityProviderUserMapDao userMapDao;
    @Autowired
    private UserBORepository userRepo;
    @Autowired
    private IdentityProviderClient identityProviderClient;

    private IdentityProviderPO identityProviderPO;

    public IdentityProviderBOImpl(IdentityProviderPO identityProviderPO)
    {
        this.identityProviderPO = identityProviderPO;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        IdentityProviderBOImpl other = (IdentityProviderBOImpl)obj;
        if (identityProviderPO == null)
        {
            if (other.identityProviderPO != null) return false;
        }
        else if (!identityProviderPO.equals(other.identityProviderPO)) return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((identityProviderPO == null) ? 0 : identityProviderPO.hashCode());
        return result;
    }

    @Override
    public IdentityProviderRef getRef()
    {
        return IdentityProviderRef.valueOfGlobal(identityProviderPO.getGlobalRef());
    }

    @Override
    public void remove()
    {
        identityProviderDao.delete(identityProviderPO);
        identityProviderPO = null;
    }

    public OIDCUserInfo getUserInfo(String idToken)
    {
        JWTClaimsSet claimsSet;
        try
        {
            claimsSet = getClaimsFromIdToken(idToken);
        }
        catch(ParseException e)
        {
            throw new IllegalStateException("can't parse id token.", e);
        }
        return map(claimsSet);
    }

    private OIDCUserInfo map(JWTClaimsSet claimsSet)
    {
        OIDCUserInfo info = new OIDCUserInfo();
        info.setSubject(claimsSet.getSubject());
        String fullName = getClaim(claimsSet, "name");
        info.setFullName(fullName);
        info.setFamilyName(getClaim(claimsSet, "family_name"));
        info.setGivenName(getClaim(claimsSet, "given_name"));
        if (getClaim(claimsSet, "email") != null)
        {
            info.seteMail(getClaim(claimsSet, "email"));
        }
        else if (getClaim(claimsSet, "upn") != null)
        {
            info.seteMail(getClaim(claimsSet, "upn"));
        }
        if (getClaim(claimsSet, "preferred_username") != null)
        {
            info.setPreferredUsername(getClaim(claimsSet, "preferred_username"));
        }
        else if (getClaim(claimsSet, "unique_name") != null)
        {
            info.setPreferredUsername(getClaim(claimsSet, "unique_name"));
        }
        return info;
    }

    private String getClaim(JWTClaimsSet claimsSet, String claim)
    {
        if (claimsSet.getClaims().keySet().contains(claim))
        {
            try
            {
                return claimsSet.getStringClaim(claim);
            }
            catch(ParseException e)
            {
                throw new IllegalStateException("can't parse id token.", e);
            }
        }
        return null;
    }

    @Transactional
    private UserRef createUser(String subject, String userName)
    {
        UserRef userRef = UserRef.localRef(UUID.randomUUID().toString());
        UserBO user = userRepo.createUser(userRef);
        user.setNickName(userName);
        IdentityProviderUserMapPO map = new IdentityProviderUserMapPO();
        map.setIdentityProviderID(identityProviderPO.getInternalId());
        map.setSubject(subject);
        map.setUserRef(userRef.getGlobalRef());
        userMapDao.save(map);
        return userRef;
    }

    private JWTClaimsSet getClaimsFromIdToken(String idToken) throws ParseException
    {
        JWT jwt = JWTParser.parse(idToken);
        JWTClaimsSet claimSet = jwt.getJWTClaimsSet();
        logger.debug("subject: {}, claims: {}", claimSet.getSubject(), claimSet.getClaims());
        return claimSet;
    }

    @Override
    public String getName()
    {
        return identityProviderPO.getName();
    }

    @Override
    public IdentityProviderDataRO getData()
    {
        IdentityProviderMetaDataResponse metaData = getMetaData();
        IdentityProviderDataRO result = new IdentityProviderDataRO();
        result.setName(identityProviderPO.getName());
        result.setClientId(identityProviderPO.getPublicClientId());
        result.setOpenIDConfigurationURI(identityProviderPO.getOpenIDConfigurationURI());
        result.setAuthenticationURI(
                        identityProviderPO.getPublicAuthenticationURI() == null ? metaData.getAuthorizationEndpoint()
                                        : identityProviderPO.getPublicAuthenticationURI());
        result.setTokenURI(identityProviderPO.getPublicTokenURI() == null ? metaData.getTokenEndpoint()
                        : identityProviderPO.getPublicTokenURI());
        result.setTokenIssuer(identityProviderPO.getPublicTokenIssuer() == null ? metaData.getIssuer()
                        : identityProviderPO.getPublicTokenIssuer());
        result.setUserInfoURI(identityProviderPO.getPublicUserInfoURI() == null ? metaData.getUserInfoEndpoint()
                        : identityProviderPO.getPublicUserInfoURI());

        result.setPrivateClientId(identityProviderPO.getPrivateClientId());
        result.setPrivateClientSecret(identityProviderPO.getPrivateClientSecret().isBlank() ? "(empty)" : "(provided)");
        return result;
    }

    private ClientRegistration getClientRegistration(String redirectUri)
    {
        IdentityProviderMetaDataResponse metaData = getMetaData();
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(identityProviderPO.getGlobalRef());
        builder.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUri(redirectUri);
        builder.scope(metaData.getSupportedScopes());
        builder.authorizationUri(metaData.getAuthorizationEndpoint());
        builder.tokenUri(metaData.getTokenEndpoint());
        builder.jwkSetUri(metaData.getJwkSetUri());
        builder.issuerUri(metaData.getIssuer());
        builder.userInfoUri(metaData.getUserInfoEndpoint());
        builder.userNameAttributeName(IdTokenClaimNames.SUB);
        builder.clientId(identityProviderPO.getPrivateClientId());
        builder.clientId(identityProviderPO.getPrivateClientSecret());
        builder.clientName(identityProviderPO.getName());
        return builder.build();
    }

    private IdentityProviderMetaDataResponse getMetaData()
    {
        return identityProviderPO.getOpenIDConfigurationURI() == null ? new IdentityProviderMetaDataResponse()
                        : identityProviderClient.getMetaData(identityProviderPO.getOpenIDConfigurationURI());
    }
}
