package de.hatoka.poker.table.capi.business;

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

import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class SeatBORepositoryTest
{
    private static final TableRef TABLE_REF_ONE = TableRef.localRef("table-one");

    @Autowired
    private TableBORepository tableRepository;

    private SeatBORepository repository;

    @BeforeEach 
    public void createTable()
    {
        repository = tableRepository.createTable(TABLE_REF_ONE, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT).getSeatRepository();
    }

    @AfterEach
    public void deleteRepo()
    {
        tableRepository.clear();
    }

    @Test
    public void testCrud()
    {
        SeatBO seat1 = repository.createSeat();
        SeatBO seat2 = repository.createSeat();
        Collection<SeatBO> seats = repository.getSeats();
        assertEquals(2, seats.size());
        assertTrue(seats.contains(seat1));
        assertTrue(seats.contains(seat2));
        seat1.remove();
        seats = repository.getSeats();
        assertEquals(1, seats.size());
        seat2.remove();
        seats = repository.getSeats();
        assertTrue(seats.isEmpty());
    }

}
