package de.hatoka.poker.security;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import de.hatoka.common.capi.rest.RestControllerErrorSupport;
import de.hatoka.oidc.capi.business.IdentityProviderBO;
import de.hatoka.oidc.capi.business.IdentityProviderBORepository;
import de.hatoka.oidc.capi.business.IdentityProviderRef;
import de.hatoka.oidc.capi.business.TokenUtils;
import de.hatoka.oidc.capi.remote.OIDCUserInfo;
import de.hatoka.oidc.capi.remote.TokenResponse;
import de.hatoka.oidc.internal.remote.IdentityProviderTokenResponse;
import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.remote.OAuthBotAuthenticationRO;
import de.hatoka.poker.remote.OAuthRefreshRO;
import de.hatoka.poker.remote.OAuthUserAuthenticationRO;

@RestController
@RequestMapping(value = UserTokenController.PATH_ROOT, produces = { APPLICATION_JSON_VALUE })
public class UserTokenController
{
    public static final String BEARER_PREFIX = "bearer ";
    public static final String PATH_ROOT = "/auth/users";
    public static final String PATH_SUB_TOKEN = "/token";
    public static final String PATH_SUB_REFRESH = "/refresh";

    @Autowired
    private IdentityProviderBORepository idpRepository;
    @Autowired
    private PlayerBORepository playerRepository;
    @Autowired
    private RestControllerErrorSupport errorSupport;
    @Autowired
    private TokenUtils tokenUtils;

    @PostMapping(value = PATH_SUB_TOKEN, consumes = { APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse generateTokenForUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken, @RequestBody OAuthUserAuthenticationRO input, UriComponentsBuilder uriBuilder)
    {
        Optional<IdentityProviderBO> opt = getIdentityProvider(input.getIdpRef());
        if (!opt.isPresent())
        {
            errorSupport.throwNotFoundException("idp.notfound", input.getIdpRef());
        }
        IdentityProviderBO idp = opt.get();
        if (!bearerToken.toLowerCase().startsWith(BEARER_PREFIX))
        {
            errorSupport.throwNotFoundException("idp.bearer.idtoken.notfound", bearerToken);
        }
        String idToken = bearerToken.substring(BEARER_PREFIX.length()).trim();
        OIDCUserInfo userInfo = idp.getUserInfo(idToken);
        return tokenUtils.createTokenForSubject(userInfo.getSubject());
    }

    @PostMapping(value = PATH_SUB_REFRESH, consumes = { APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse createTokenFromRefresh(@RequestBody OAuthRefreshRO input)
    {
        return tokenUtils.createTokenFromRefreshToken(input.getRefreshToken());
    }

    private Optional<IdentityProviderBO> getIdentityProvider(String idpLocalRefString)
    {
        IdentityProviderRef idpRef = IdentityProviderRef.valueOfLocal(idpLocalRefString);
        return idpRepository.findIdentityProvider(idpRef);
    }
}
