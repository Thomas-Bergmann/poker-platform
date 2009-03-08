package de.hatoka.poker.table.capi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class SeatBOTest
{
    private static final TableRef TABLE_REF_ONE = TableRef.localRef("table-one");
    private static final UserRef USER_REF_ONE = UserRef.localRef("user-one");

    @Autowired
    private TableBORepository tableRepository;
    @Autowired
    private PlayerBORepository playerRepository;

    private SeatBORepository repository;
    private PlayerBO player;

    @BeforeEach
    public void createTable()
    {
        deleteRepo();
        repository = tableRepository.createTable(TABLE_REF_ONE, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT)
                                    .getSeatRepository();
        player = playerRepository.createHumanPlayer(USER_REF_ONE);
    }

    @AfterEach
    public void deleteRepo()
    {
        tableRepository.clear();
        playerRepository.clear();
    }

    @Test
    public void testSeat() throws IOException
    {
        SeatBO seat = repository.createSeat();
        assertEquals(TABLE_REF_ONE, seat.getRef().getTableRef());
        seat.join(player);
        assertEquals(0, player.getBalance());
        seat.buyin(500);
        assertEquals(-500, playerRepository.getPlayer(player.getInternalId()).getBalance());
        assertEquals(500, seat.getAmountOfCoinsOnSeat());

    }

    @Test
    public void testDoNotJoinTwice() throws IOException
    {
        SeatBO seat1 = repository.createSeat();
        SeatBO seat2 = repository.createSeat();

        seat1.join(player, 300);
        assertThrows(IllegalStateException.class, () -> {
            seat2.join(player, 500);
        });
        assertEquals(1, repository.getReadySeats().size());
    }
}
