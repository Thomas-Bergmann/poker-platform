package de.hatoka.poker.table.internal.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.table.PlayerActions;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.IllegalGameEventException;
import de.hatoka.poker.table.capi.event.game.DealerInGame;
import de.hatoka.poker.table.capi.event.game.PlayerFactory;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class PlayerTest
{
    private static final DealerResourceLoader LOADER = new DealerResourceLoader();
    private static final SeatRef seatZeroRef = SeatRef.globalRef("seat:0@table:3c5ededf-b795-4abd-bd18-68ae2a183a7f");
    private static final SeatRef seatThreeRef = SeatRef.globalRef("seat:3@table:3c5ededf-b795-4abd-bd18-68ae2a183a7f");

    @Autowired
    private PlayerFactory playerFactory;

    private GameInfo getGameInfo(String resource, int numberOfEvents) throws URISyntaxException, IOException
    {
        return LOADER.load(getResourceURI(resource), numberOfEvents);
    }

    private URI getResourceURI(String resourcePath) throws URISyntaxException
    {
        return getClass().getResource(resourcePath).toURI();
    }

    @Test
    @Timeout(10)
    public void testBetNothing() throws Exception
    {
        GameInfo gameInfo = getGameInfo("PlayerTest_testBetNothing.csv", 12);
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerZero = playerFactory.get(gameInfo, seatZeroRef);
        PlayerActions playerThree = playerFactory.get(gameInfo, seatThreeRef);

        // initial 1540 - 10 big - 20 call - 30 bet
        assertEquals(1480, gameInfo.getCoinsOnSeat(seatThreeRef));
        // initial 465 - 5 small - 25 bet - 60 raise
        assertEquals(375, gameInfo.getCoinsOnSeat(seatZeroRef));

        assertEquals(30, gameInfo.getCoinsInPlay().get(seatThreeRef));
        assertEquals(60, gameInfo.getCoinsInPlay().get(seatZeroRef));
        // last action was seat 0 raises to 60
        assertEquals(seatThreeRef, gameInfo.getSeatHasAction().get());
        // wrong player made action
        assertThrows(IllegalGameEventException.class, () -> {playerZero.betTo(240);});
        // less than 0
        assertThrows(IllegalGameEventException.class, () -> {playerThree.betTo(40);});
        // less than minimal raise
        assertThrows(IllegalGameEventException.class, () -> {playerThree.betTo(70);});
        // can't check has less than max
        assertThrows(IllegalGameEventException.class, () -> {playerThree.check(); });
        playerThree.call();
        assertEquals(60, gameInfo.getCoinsInPlay().get(seatZeroRef));
        assertEquals(60, gameInfo.getCoinsInPlay().get(seatThreeRef));
    }

    @Test
    @Timeout(10)
    public void testMinRaise() throws Exception
    {
        GameInfo gameInfo = getGameInfo("PlayerTest_testBetNothing.csv", 11);
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerZero = playerFactory.get(gameInfo, seatZeroRef);

        // initial 1540 - 10 big - 20 call - 30 bet
        assertEquals(1480, gameInfo.getCoinsOnSeat(seatThreeRef));
        // initial 465 - 5 small - 25 bet
        assertEquals(435, gameInfo.getCoinsOnSeat(seatZeroRef));

        assertEquals(30, gameInfo.getCoinsInPlay().get(seatThreeRef));
        assertEquals(0, gameInfo.getCoinsInPlay().getOrDefault(seatZeroRef, 0));
        playerZero.betTo(90);
    }

    @Test
    @Timeout(10)
    public void testCallToMuch() throws Exception
    {
        GameInfo gameInfo = getGameInfo("PlayerTest_testCallToMuch.csv", 22);
        gameInfo.logEvents();
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerZero = playerFactory.get(gameInfo, seatZeroRef);

        assertEquals(225, gameInfo.getCoinsOnSeat(seatZeroRef));
        assertEquals(1060, gameInfo.getCoinsOnSeat(seatThreeRef));
        
        assertEquals(240, gameInfo.getCoinsInPlay().getOrDefault(seatThreeRef, 0));
        assertEquals(0, gameInfo.getCoinsInPlay().getOrDefault(seatZeroRef, 0));

        // needs to call 240 but has 225
        playerZero.call();
        assertEquals(0, gameInfo.getCoinsOnSeat(seatZeroRef));
    }
}
