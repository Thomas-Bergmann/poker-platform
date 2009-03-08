package de.hatoka.poker.table.capi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class TableBOTest
{
    private static final String NAME = "TableBOTest";
    private static final TableRef TABLE_REF_ONE = TableRef.localRef(NAME);
    private static final UserRef USER_REF_ONE = UserRef.localRef("one");
    private static final UserRef USER_REF_TWO = UserRef.localRef("two");

    @Autowired
    private TableBORepository tableRepo;
    @Autowired
    private PlayerBORepository playerRepo;

    @BeforeEach
    public void createPlayer()
    {
        deleteRepo();
        playerRepo.createHumanPlayer(USER_REF_ONE);
        playerRepo.createHumanPlayer(USER_REF_TWO);
    }

     @AfterEach
    public void deleteRepo()
    {
        tableRepo.clear();
        playerRepo.clear();
    }

    @Test
    public void testCRUD() throws IOException
    {
        TableBO table = tableRepo.createTable(TABLE_REF_ONE, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT);
        assertEquals(NAME, table.getName());
        assertEquals("table:TableBOTest", table.getRef().getGlobalRef());
    }
}
