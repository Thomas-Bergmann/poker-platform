package de.hatoka.poker.player.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

import de.hatoka.common.capi.rest.test.TestSecurityConfiguration;
import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.remote.OAuthBotAuthenticationRO;
import de.hatoka.poker.remote.OAuthRefreshRO;
import de.hatoka.poker.remote.OAuthTokenResponse;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.player.PlayerTestApplication;
import tests.de.hatoka.poker.player.PlayerTestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { PlayerTestApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { PlayerTestConfiguration.class, TestSecurityConfiguration.class })
@ActiveProfiles("test")
public class BotTokenControllerTest
{
    private static final String TOKEN_PREFIX = "ey";
    private static final UserRef OWNER_REF = UserRef.localRef("owner-one");

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PlayerBORepository repository;

    @BeforeEach
    public void createPlayer()
    {
        deleteRepo();
        repository.createHumanPlayer(OWNER_REF);
    }

    @AfterEach
    public void deleteRepo()
    {
        repository.clear();
    }

    @Test
    public void testAccessToken()
    {
        PlayerBO bot = repository.createBotPlayer(OWNER_REF, "bot-1");
        OAuthBotAuthenticationRO auth = new OAuthBotAuthenticationRO();
        auth.setApiKey(bot.getApiKey());
        auth.setBotRef(bot.getRef().getGlobalRef());
        OAuthTokenResponse authResponse = authBot(auth);
        assertTrue(authResponse.getExpiresIn() > (new Date()).getTime());
        assertTrue(authResponse.getNotBeforePolicy() < (new Date()).getTime());
        assertEquals(TOKEN_PREFIX, authResponse.getAccessToken().substring(0, 2));
    }

    @Test
    public void testRefeshToken() throws InterruptedException
    {
        PlayerBO bot = repository.createBotPlayer(OWNER_REF, "bot-1");
        OAuthBotAuthenticationRO auth = new OAuthBotAuthenticationRO();
        auth.setApiKey(bot.getApiKey());
        auth.setBotRef(bot.getRef().getGlobalRef());
        OAuthTokenResponse authResponse = authBot(auth);
        assertEquals(TOKEN_PREFIX, authResponse.getRefreshToken().substring(0, 2));
        OAuthRefreshRO refreshRequest = new OAuthRefreshRO();
        refreshRequest.setRefreshToken(authResponse.getRefreshToken());
        // wait for next second - because expiration contains only seconds (otherwise we can't test that token is newer
        // two hundred millis for different OS clocks to tick
        Thread.sleep(1200);
        OAuthTokenResponse authRefreshResponse = authBot(refreshRequest);
        assertEquals(TOKEN_PREFIX, authRefreshResponse.getAccessToken().substring(0, 2));
        // got new access token
        assertNotEquals(authResponse.getAccessToken(), authRefreshResponse.getAccessToken());
        // refreshed token is newer 
        assertTrue(authResponse.getExpiresIn() < authRefreshResponse.getExpiresIn());
    }

    private OAuthTokenResponse authBot(OAuthBotAuthenticationRO auth)
    {
        return restTemplate.exchange(BotTokenController.PATH_ROOT + BotTokenController.PATH_SUB_BOT_TOKEN, HttpMethod.POST,
                        new HttpEntity<>(auth), OAuthTokenResponse.class).getBody();
    }
    private OAuthTokenResponse authBot(OAuthRefreshRO auth)
    {
        return restTemplate.exchange(BotTokenController.PATH_ROOT + BotTokenController.PATH_SUB_BOT_REFRESH, HttpMethod.POST,
                        new HttpEntity<>(auth), OAuthTokenResponse.class).getBody();
    }
}
