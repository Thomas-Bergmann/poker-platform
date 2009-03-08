package de.hatoka.poker.player.capi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

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
public class PlayerBOTest
{
    private static final String NAME = "PlayerBOTest";
    private static final UserRef OWNER_REF = UserRef.localRef("owner-one");

    @Autowired
    private PlayerBORepository repository;

    @BeforeEach @AfterEach
    public void deleteRepo()
    {
        repository.clear();
    }

    @Test
    public void testPlayer() throws IOException
    {
        PlayerBO player = repository.createBotPlayer(OWNER_REF, NAME);
        assertEquals(NAME, player.getNickName());
        assertEquals(PlayerType.COMPUTE, player.getType());
        assertEquals("bot:PlayerBOTest@owner-one", player.getRef().getGlobalRef());
    }
}
