package de.hatoka.poker.table.internal.event;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.hatoka.poker.base.Deck;
import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import de.hatoka.poker.table.internal.json.GameEventType;
import de.hatoka.user.capi.business.UserRef;

class GameStartEventTest
{

    private static final TableRef TABLE_REF = TableRef.localRef("table-one");
    private static final Integer SEAT_POSITION = 0;
    private static final PlayerRef PLAYER_REF = PlayerRef.botRef(UserRef.localRef("user-one"), "bot-one");
    private static final SeatRef DEALER_REF = SeatRef.localRef(TABLE_REF, SEAT_POSITION);

    @Test
    void test()
    {
        Deck.initializeRandom(123456L);
        StartEvent event = new StartEvent();
        SeatBO seat = getSeat(SEAT_POSITION, PLAYER_REF);

        List<SeatBO> seats = Arrays.asList(seat);
        event.setSeats(seats);
        event.setOnButton(DEALER_REF);
        event.setDeck(Deck.generate());
        
        GameEventType type = GameEventType.valueOf(StartEvent.class);
        String asString = type.serialize(event);
        StartEvent serEvent = (StartEvent) type.deserialize(asString);
        assertEquals(DEALER_REF, serEvent.getOnButton());
        assertEquals(PLAYER_REF, serEvent.getPlayers().get(DEALER_REF));
    }

    private SeatBO getSeat(Integer position, PlayerRef playerRef)
    {
        SeatBO seat = Mockito.mock(SeatBO.class);
        SeatRef seatRef = SeatRef.localRef(TABLE_REF, position);
        Mockito.when(seat.getRef()).thenReturn(seatRef);
        Mockito.when(seat.getPosition()).thenReturn(position);
        PlayerBO player = Mockito.mock(PlayerBO.class);
        Mockito.when(player.getRef()).thenReturn(playerRef);
        Mockito.when(seat.getPlayer()).thenReturn(Optional.of(player));
        return seat;
    }
}
