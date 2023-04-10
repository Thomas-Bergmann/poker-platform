package de.hatoka.poker.table.internal.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.game.DealerInGame;
import de.hatoka.poker.table.capi.event.game.PlayerFactory;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import de.hatoka.poker.table.capi.event.history.seat.BetEvent;
import de.hatoka.poker.table.capi.event.history.seat.CallEvent;
import de.hatoka.poker.table.capi.event.history.seat.CheckEvent;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class DealerInGameImplTest
{
    private static final DealerResourceLoader LOADER = new DealerResourceLoader();
    private static final SeatRef seatZeroRef = SeatRef.globalRef("seat:0@table:DealerInfoTest");
    private static final SeatRef seatOneRef = SeatRef.globalRef("seat:1@table:DealerInfoTest");
    private static final SeatRef seatTwoRef = SeatRef.globalRef("seat:2@table:DealerInfoTest");

    @Autowired
    private PlayerFactory playerFactory;

    private GameInfo getGameInfo(String resource) throws URISyntaxException, IOException
    {
        return LOADER.load(getResourceURI(resource));
    }

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
    public void testSimplePlay() throws Exception
    {
        GameInfo gameInfo = getGameInfo("DealerTest_testSimplePlay.csv");
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatZeroRef);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatOneRef);

        playerOne.betTo(50);
        playerTwo.call();
        dealer.collectCoins();
        dealer.flop();
        playerTwo.check();
        playerOne.check();
        dealer.collectCoins();
        dealer.turn();
        playerTwo.check();
        playerOne.check();
        dealer.collectCoins();
        dealer.river();
        playerTwo.check();
        playerOne.betTo(100);
        playerTwo.fold();
        dealer.collectCoins();
        assertFalse(dealer.canTransfer());
        dealer.showDown();
        assertEquals(550, gameInfo.getCoinsOnSeat(seatZeroRef));
        assertEquals(150, gameInfo.getCoinsOnSeat(seatOneRef));
        assertTrue(dealer.canTransfer());
        assertEquals(550, dealer.getTransfer().getCoinsOnSeatAfterGame().get(seatZeroRef));
        assertEquals(150, dealer.getTransfer().getCoinsOnSeatAfterGame().get(seatOneRef));
    }

    @Test
    @Timeout(10)
    public void testAllInPlaySecondHasMoreMoney() throws Exception
    {
        GameInfo gameInfo = getGameInfo("DealerTest_testAllInPlaySecondHasMoreMoney.csv");
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatZeroRef);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatOneRef);

        assertEquals(195, gameInfo.getCoinsOnSeat(seatZeroRef));

        playerOne.betTo(50);
        playerTwo.call();
        dealer.collectCoins();
        dealer.flop();
        playerTwo.check();
        playerOne.betTo(100);
        playerTwo.call();
        dealer.collectCoins();
        dealer.turn();
        dealer.river();
        dealer.showDown();
        assertEquals(350, gameInfo.getCoinsOnSeat(seatZeroRef));
        assertEquals(350, gameInfo.getCoinsOnSeat(seatOneRef));
    }

    @Test
    @Timeout(10)
    public void testAllInPlayCallerHasLessMoney() throws Exception
    {
        GameInfo gameInfo = getGameInfo("DealerTest_testAllInPlayCallerHasLessMoney.csv");
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerZero = playerFactory.get(gameInfo, seatZeroRef);
        PlayerActions playerOne = playerFactory.get(gameInfo, seatOneRef);

        playerZero.betTo(50);
        playerOne.call();
        dealer.collectCoins();
        assertEquals(100, gameInfo.getPotSize());
        dealer.flop();
        playerOne.check();
        playerZero.allIn();
        playerOne.allIn();
        dealer.collectCoins();
        assertEquals(700, gameInfo.getPotSize());
        dealer.turn();
        dealer.river();
        dealer.showDown();
        assertEquals(700, gameInfo.getCoinsOnSeat(seatZeroRef));
        assertEquals(0, gameInfo.getCoinsOnSeat(seatOneRef));
    }

    @Test
    @Timeout(10)
    public void testDoWhatEverYouNeed() throws Exception
    {
        GameInfo gameInfo = getGameInfo("DealerTest_testDoWhatEverYouNeed.csv");
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatZeroRef);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatOneRef);

        assertNotNull(gameInfo.getPot());
        playerOne.betTo(50);
        playerTwo.call();
        assertTrue(gameInfo.hasDealerAction());
        dealer.doWhatEverYouNeed();
        // dealer.collectCoins();
        // dealer.flop();
        playerTwo.check();
        playerOne.check();
        assertTrue(gameInfo.hasDealerAction());
        dealer.doWhatEverYouNeed();
        // dealer.collectCoins();
        // dealer.turn();
        playerTwo.check();
        playerOne.check();
        assertTrue(gameInfo.hasDealerAction());
        dealer.doWhatEverYouNeed();
        // dealer.collectCoins();
        // dealer.river();
        playerTwo.check();
        playerOne.betTo(100);
        playerTwo.fold();
        assertTrue(gameInfo.hasDealerAction());
        dealer.doWhatEverYouNeed();
        // dealer.collectCoins();
        // dealer.showDown();
        assertEquals(550, gameInfo.getCoinsOnSeat(seatZeroRef));
        assertEquals(150, gameInfo.getCoinsOnSeat(seatOneRef));
    }

    @Test
    @Timeout(10)
    public void testFirstRoundAfterBet() throws IOException, URISyntaxException
    {
        GameInfo dealerInfo = getGameInfo("DealerTest_testFirstRoundAfterBet.csv", 9);
        List<GameEvent> events = dealerInfo.getEvents(GameEvent.class).toList();
        assertEquals(StartEvent.class, events.get(0).getClass());
        assertEquals(CallEvent.class, events.get(6).getClass());
        assertEquals(BetEvent.class, events.get(7).getClass());
        assertEquals(CallEvent.class, events.get(8).getClass());
        // third player has action
        assertFalse(dealerInfo.hasDealerAction());
    }

    @Test
    @Timeout(10)
    public void testCheckWithThree() throws Exception
    {
        GameInfo dealerInfo = getGameInfo("DealerTest_testCheckWithThree.csv");
        DealerInGame dealer = new DealerInGameImpl(dealerInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(dealerInfo, seatZeroRef);
        PlayerActions playerTwo = playerFactory.get(dealerInfo, seatOneRef);
        PlayerActions playerThree = playerFactory.get(dealerInfo, seatTwoRef);

        // start with one (button is one, small two, big three)
        assertEquals(seatZeroRef, dealerInfo.getSeatHasAction().get());
        playerOne.call();
        assertEquals(seatOneRef, dealerInfo.getSeatHasAction().get());
        playerTwo.call();
        assertFalse(dealerInfo.hasDealerAction());
        assertEquals(seatTwoRef, dealerInfo.getSeatHasAction().get());
        playerThree.check();
        assertTrue(dealerInfo.hasDealerAction());
        dealer.doWhatEverYouNeed();
        // seat two is behind button and starts second round after turn
        assertEquals(seatOneRef, dealerInfo.getSeatHasAction().get());
        playerTwo.check();
        assertEquals(seatTwoRef, dealerInfo.getSeatHasAction().get());
        playerThree.check();
        assertEquals(seatZeroRef, dealerInfo.getSeatHasAction().get());
        playerOne.check();
        assertTrue(dealerInfo.hasDealerAction());
        dealer.doWhatEverYouNeed();
        // seat two is behind button and starts third round after river
        assertEquals(seatOneRef, dealerInfo.getSeatHasAction().get());
        playerTwo.check();
        assertEquals(seatTwoRef, dealerInfo.getSeatHasAction().get());
        playerThree.check();
        assertEquals(seatZeroRef, dealerInfo.getSeatHasAction().get());
        playerOne.check();
        assertTrue(dealerInfo.hasDealerAction());
    }

    @Test
    @Timeout(10)
    public void testDealerActionIfButtonHasFolded() throws IOException, URISyntaxException
    {
        GameInfo dealerInfo = getGameInfo("DealerTest_testDealerActionIfButtonHasFolded.csv", 25);
        List<GameEvent> events = dealerInfo.getEvents(GameEvent.class).toList();
        assertEquals(StartEvent.class, events.get(0).getClass());
        assertEquals(CheckEvent.class, events.get(23).getClass());
        assertEquals(CheckEvent.class, events.get(24).getClass());
        assertTrue(dealerInfo.hasDealerAction());
    }

    @Test
    @Timeout(10)
    public void testDealerNoActive() throws IOException, URISyntaxException
    {
        GameInfo dealerInfo = getGameInfo("DealerTest_noActive.csv");
        List<GameEvent> events = dealerInfo.getEvents(GameEvent.class).toList();
        assertEquals(StartEvent.class, events.get(0).getClass());
        assertTrue(dealerInfo.getSeatHasAction().isPresent());
        // dealer needs to check that this seat has not left the table
        assertEquals(0, dealerInfo.getSeatHasAction().get().getPosition());
    }
}
