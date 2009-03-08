package de.hatoka.oidc.internal.business;

import java.text.ParseException;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;

import de.hatoka.common.capi.configuration.DateProvider;
import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.capi.business.IdentityProviderRef;
import de.hatoka.oidc.capi.event.UserTokenGeneratedEvent;
import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.oidc.capi.remote.OIDCUserInfo;
import de.hatoka.oidc.capi.remote.TokenResponse;
import de.hatoka.oidc.internal.persistence.IdentityProviderDao;
import de.hatoka.oidc.internal.persistence.IdentityProviderPO;
import de.hatoka.oidc.internal.persistence.IdentityProviderUserMapDao;
import de.hatoka.oidc.internal.persistence.IdentityProviderUserMapPO;
import de.hatoka.oidc.internal.remote.IdentityProviderClient;
import de.hatoka.oidc.internal.remote.IdentityProviderMetaDataResponse;
import de.hatoka.user.capi.business.UserBO;
import de.hatoka.user.capi.business.UserBORepository;
import de.hatoka.user.capi.business.UserRef;

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
    private DateProvider dateProvider;
    @Autowired
    private IdentityProviderClient identityProviderClient;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

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

    @Override
    public TokenResponse generateToken(String issuer, String idToken, Set<String> scopes)
    {
        UserRef userRef = null;
        try
        {
            JWTClaimsSet claimsSet = getClaimsFromIdToken(idToken);
            userRef = getUserRef(claimsSet);
            updateUserInfo(userRef, claimsSet);
        }
        catch(ParseException e)
        {
            throw new IllegalStateException("can't parse id token.", e);
        }
        TokenResponse result = new TokenResponse();
        long validitySeconds = identityProviderPO.getTokenValidityPeriod();
        result.setExpiresIn(validitySeconds);
        result.setRefreshExpiresIn(validitySeconds * 3);
        result.setAccessToken(generateAccessJWToken(issuer, scopes, userRef, result.getExpiresIn()).serialize());
        result.setRefreshToken(
                        generateRefreshJWToken(issuer, scopes, userRef, result.getRefreshExpiresIn()).serialize());
        result.setIdToken(generateIdentityJWToken(issuer, scopes, userRef, validitySeconds).serialize());
        // inform other about token generation by user
        applicationEventPublisher.publishEvent(new UserTokenGeneratedEvent(this, userRef, result));
        return result;
    }

    private void updateUserInfo(UserRef userRef, JWTClaimsSet claimsSet)
    {
        Optional<UserBO> userOpt = userRepo.findUser(userRef);
        if (userOpt.isEmpty())
        {
            return;
        }
        UserBO user = userOpt.get();
        user.updateUserInfo(map(claimsSet));
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

    private UserRef getUserRef(JWTClaimsSet claimsSet) throws ParseException
    {
        // claims: [sub, email_verified, iss, typ, preferred_username, given_name, nonce, aud, acr, azp, auth_time,
        // name,
        // exp, session_state, iat, family_name, jti, email]
        String subject = claimsSet.getStringClaim("sub");
        String userName = claimsSet.getStringClaim("preferred_username");
        Optional<IdentityProviderUserMapPO> userMapOpt = userMapDao.findByIdentityProviderIDAndSubject(
                        identityProviderPO.getInternalId(), subject);
        UserRef userRef = null;
        if (!userMapOpt.isPresent())
        {
            userRef = createUser(subject, userName);
        }
        else
        {
            userRef = UserRef.globalRef(userMapOpt.get().getUserRef());
        }
        return userRef;
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

    private SignedJWT generateIdentityJWToken(String issuer, Set<String> scopes, UserRef userRef, long expires)
    {
        return generateAccessJWToken(issuer, scopes, userRef, expires);
    }

    private SignedJWT generateRefreshJWToken(String issuer, Set<String> scopes, UserRef userRef, long expires)
    {
        return generateAccessJWToken(issuer, scopes, userRef, expires);
    }

    private SignedJWT generateAccessJWToken(String issuer, Set<String> scopes, UserRef userRef, long expires)
    {
        String secret = identityProviderPO.getAccessTokenSecret();
        String clientid = identityProviderPO.getPublicClientId();
        if (null == clientid || null == secret || expires < 0)
        {
            throw new IllegalStateException("Unable to create JWT with wrong configuration.");
        }
        try
        {
            Date now = dateProvider.get();
            StringBuilder scopesBuilder = new StringBuilder();
            for (String scope : scopes)
            {
                scopesBuilder = scopesBuilder.append(scope).append(" ");
            }
            JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
            claimsSet.issuer(issuer);
            claimsSet.subject(userRef.getGlobalRef());
            claimsSet.audience(clientid);
            claimsSet.issueTime(now);
            claimsSet.expirationTime(new Date(now.getTime() + (expires * 1000)));
            claimsSet.notBeforeTime(now);
            claimsSet.claim("scope", scopesBuilder.toString());
            JWSSigner signer = new MACSigner(secret);
            SignedJWT result = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet.build());
            result.sign(signer);
            return result;
        }
        catch(JOSEException e)
        {
            throw new IllegalStateException("Unable to sign JWT.", e);
        }
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
    public Long getTokenValidityPeriod()
    {
        return identityProviderPO.getTokenValidityPeriod();
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
        result.setTokenValidityPeriod(identityProviderPO.getTokenValidityPeriod());

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
