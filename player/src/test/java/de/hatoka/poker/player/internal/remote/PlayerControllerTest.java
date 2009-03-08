package de.hatoka.poker.player.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import de.hatoka.common.capi.rest.test.TestSecurityConfiguration;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.remote.PlayerRO;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.player.PlayerTestApplication;
import tests.de.hatoka.poker.player.PlayerTestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { PlayerTestApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { PlayerTestConfiguration.class, TestSecurityConfiguration.class })
@ActiveProfiles("test")
public class PlayerControllerTest
{
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
    public void testGetPlayers()
    {
        List<PlayerRO> players = getPlayers();
        assertEquals(1, players.size());
        assertEquals(OWNER_REF.getGlobalRef(), players.get(0).getData().getOwnerRef());
    }

    private List<PlayerRO> getPlayers()
    {
        Map<String, String> urlParams = new HashMap<>();
        return Arrays.asList(this.restTemplate.getForObject(PlayerController.PATH_ROOT, PlayerRO[].class, urlParams));
    }
}
