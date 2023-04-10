package de.hatoka.oauth.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.oauth.capi.remote.OAuthUserAuthenticationRO;
import de.hatoka.oidc.capi.business.IdentityProviderBORepository;
import de.hatoka.oidc.capi.business.IdentityProviderRef;
import de.hatoka.oidc.capi.remote.IdentityProviderDataRO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.remote.oauth.OAuthRefreshRO;
import de.hatoka.poker.remote.oauth.OAuthTokenResponse;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.oauth.OAuthTestApplication;
import tests.de.hatoka.oauth.OAuthTestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { OAuthTestApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { OAuthTestConfiguration.class })
@ActiveProfiles("test")
public class UserTokenControllerTest
{
    private static final String TOKEN_PREFIX = "ey";
    private static final UserRef OWNER_REF = UserRef.localRef("owner-one");
    private static final IdentityProviderRef IDP_REF = IdentityProviderRef.valueOfLocal("TEST");

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PlayerBORepository repository;
    @Autowired
    private IdentityProviderBORepository idRepository;

    @BeforeEach
    public void createPlayer()
    {
        deleteRepo();
        repository.createHumanPlayer(OWNER_REF);
        IdentityProviderDataRO data = getProviderInfo(
                        "Microsoft",
                        URI.create("https://login.microsoftonline.com/tenantid/oauth2/v2.0/.well-known/openid-configuration"),
                        URI.create("https://login.microsoftonline.com/tenantid/oauth2/v2.0/authorize"),
                        URI.create("https://login.microsoftonline.com/tenantid/oauth2/v2.0/token"),
                        "azure", "secret");
        idRepository.createIdentityProvider(IDP_REF, data);
    }

    @AfterEach
    public void deleteRepo()
    {
        repository.clear();
    }

    /**
     * Checks authentication with identity providers
     * TODO mock identity token from TEST identity provider
     */
    @Test @Disabled
    public void testAccessToken()
    {
        OAuthUserAuthenticationRO auth = new OAuthUserAuthenticationRO();
        auth.setIdpRef(IDP_REF.getLocalRef());
        OAuthTokenResponse authResponse = authUser(auth);
        assertTrue(authResponse.getExpiresIn() > (new Date()).getTime());
        assertTrue(authResponse.getNotBeforePolicy() < (new Date()).getTime());
        assertEquals(TOKEN_PREFIX, authResponse.getAccessToken().substring(0, 2));
    }

    /**
     * Checks authentication with identity providers via refresh token
     * TODO mock identity token from TEST identity provider
     */
    @Test @Disabled
    public void testRefeshToken() throws InterruptedException
    {
        OAuthUserAuthenticationRO auth = new OAuthUserAuthenticationRO();
        auth.setIdpRef(IDP_REF.getLocalRef());
        OAuthTokenResponse authResponse = authUser(auth);
        assertEquals(TOKEN_PREFIX, authResponse.getRefreshToken().substring(0, 2));
        OAuthRefreshRO refreshRequest = new OAuthRefreshRO();
        refreshRequest.setRefreshToken(authResponse.getRefreshToken());
        // wait for next second - because expiration contains only seconds (otherwise we can't test that token is newer
        // two hundred millis for different OS clocks to tick
        Thread.sleep(1200);
        OAuthTokenResponse authRefreshResponse = authRefresh(refreshRequest);
        assertEquals(TOKEN_PREFIX, authRefreshResponse.getAccessToken().substring(0, 2));
        // got new access token
        assertNotEquals(authResponse.getAccessToken(), authRefreshResponse.getAccessToken());
        // refreshed token is newer 
        assertTrue(authResponse.getExpiresIn() < authRefreshResponse.getExpiresIn());
    }

    private OAuthTokenResponse authUser(OAuthUserAuthenticationRO auth)
    {
        return restTemplate.exchange(UserTokenController.PATH_ROOT + UserTokenController.PATH_SUB_TOKEN, HttpMethod.POST,
                        new HttpEntity<>(auth), OAuthTokenResponse.class).getBody();
    }
    private OAuthTokenResponse authRefresh(OAuthRefreshRO auth)
    {
        return restTemplate.exchange(UserTokenController.PATH_ROOT + UserTokenController.PATH_SUB_REFRESH, HttpMethod.POST,
                        new HttpEntity<>(auth), OAuthTokenResponse.class).getBody();
    }

    private IdentityProviderDataRO getProviderInfo(String name, URI openIdConfigURI, URI authURI, URI tokenURI, String client, String secret)
    {
        IdentityProviderDataRO data = new IdentityProviderDataRO();
        data.setName(name);
        data.setClientId(client);
        data.setPrivateClientId(client);
        data.setPrivateClientSecret(secret);
        data.setOpenIDConfigurationURI(openIdConfigURI.toString());
        data.setAuthenticationURI(authURI.toString());
        data.setTokenURI(tokenURI.toString());
        return data;
    }
}
