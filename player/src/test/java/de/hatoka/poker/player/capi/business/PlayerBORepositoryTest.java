package de.hatoka.poker.player.capi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.player.PlayerTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { PlayerTestConfiguration.class })
public class PlayerBORepositoryTest
{
    private static final UserRef OWNER_REF = UserRef.localRef("owner-one");

    @Autowired
    private PlayerBORepository repository;

    @BeforeEach
    @AfterEach
    public void deleteRepo()
    {
        repository.clear();
    }

    @Test
    public void testCrud()
    {
        PlayerBO player1 = repository.createBotPlayer(OWNER_REF, "bot1");
        PlayerBO player2 = repository.createBotPlayer(OWNER_REF, "bot2");
        Collection<PlayerBO> projects = repository.getAllPlayers();
        assertEquals(2, projects.size());
        assertTrue(projects.contains(player1));
        assertTrue(projects.contains(player2));
        player1.remove();
        projects = repository.getAllPlayers();
        assertEquals(1, projects.size());
        player2.remove();
        projects = repository.getAllPlayers();
        assertTrue(projects.isEmpty());
    }

}
