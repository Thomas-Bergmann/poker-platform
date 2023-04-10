package de.hatoka.poker.player.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.player.capi.remote.BotRO;
import de.hatoka.poker.player.capi.remote.PlayerDataRO;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.player.PlayerTestApplication;
import tests.de.hatoka.poker.player.PlayerTestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { PlayerTestApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { PlayerTestConfiguration.class })
@ActiveProfiles("test")
public class BotControllerTest
{
    private static final UserRef OWNER_REF = UserRef.localRef("owner-one");
    private static final PlayerRef PLAYER_REF1 = PlayerRef.botRef(OWNER_REF, "name-1");
    private static final PlayerRef PLAYER_REF2 = PlayerRef.botRef(OWNER_REF, "name-2");

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
    public void createBotAndDelete()
    {
        putBot(PLAYER_REF1);

        BotRO ro = getBot(PLAYER_REF1);
        assertNotNull(ro, "bot created and found");
        assertNotNull(ro.getData(), "bot contains data");
        assertNotNull(ro.getInfo(), "bot contains info");
        assertEquals("name-1", ro.getData().getNickName());
        assertNotNull(ro.getData().getApiKey(), "bot contains api key");
        deleteBot(PLAYER_REF1);
    }

    @Test
    public void testGetBots()
    {
        putBot(PLAYER_REF1);
        putBot(PLAYER_REF2);

        List<BotRO> bots = getBots();
        assertEquals(2, bots.size());
        deleteBot(PLAYER_REF1);
        deleteBot(PLAYER_REF2);
    }

    private List<BotRO> getBots()
    {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put(BotController.PATH_VAR_PLAYERID, OWNER_REF.getLocalRef());
        return Arrays.asList(this.restTemplate.getForObject(BotController.PATH_ROOT, BotRO[].class, urlParams));
    }

    private Map<String, String> createURIParameter(PlayerRef ref)
    {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put(BotController.PATH_VAR_PLAYERID, ref.getUserRef().getLocalRef());
        urlParams.put(BotController.PATH_VAR_BOTID, ref.getName());
        return urlParams;
    }

    private BotRO getBot(PlayerRef ref)
    {
        return this.restTemplate.getForObject(BotController.PATH_BOT, BotRO.class, createURIParameter(ref));
    }

    private void putBot(PlayerRef ref)
    {
        PlayerDataRO data = new PlayerDataRO();
        data.setOwnerRef(OWNER_REF.getGlobalRef());
        this.restTemplate.put(BotController.PATH_BOT, data, createURIParameter(ref));
    }

    private void deleteBot(PlayerRef ref)
    {
        this.restTemplate.delete(BotController.PATH_BOT, createURIParameter(ref));
    }
}
