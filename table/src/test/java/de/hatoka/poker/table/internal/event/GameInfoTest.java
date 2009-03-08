package de.hatoka.poker.table.internal.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

import de.hatoka.poker.base.Card;
import de.hatoka.poker.table.PlayerActions;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.game.DealerInGame;
import de.hatoka.poker.table.capi.event.game.PlayerFactory;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class GameInfoTest
{
    private static final DealerResourceLoader LOADER = new DealerResourceLoader();
    private static final SeatRef seatOne = SeatRef.globalRef("seat:0@table:DealerInfoTest");
    private static final SeatRef seatTwo = SeatRef.globalRef("seat:1@table:DealerInfoTest");
    private static final SeatRef seatThree = SeatRef.globalRef("seat:2@table:DealerInfoTest");

    @Autowired
    private PlayerFactory playerFactory;

    private GameInfo getGameInfo() throws URISyntaxException, IOException
    {
        return LOADER.load(getResourceURI("GameInfoTest_StartEvent2.csv"));
    }

    private GameInfo getGameInfoPlayers3() throws URISyntaxException, IOException
    {
        return LOADER.load(getResourceURI("GameInfoTest_StartEvent3.csv"));
    }

    private URI getResourceURI(String resourcePath) throws URISyntaxException
    {
        return getClass().getResource(resourcePath).toURI();
    }

    @Test
    @Timeout(10)
    public void testAfterStartDealerMustCollectBlind() throws IOException, URISyntaxException
    {
        GameInfo gameInfo = getGameInfo();
        List<GameEvent> events = gameInfo.getEvents(GameEvent.class).toList();
        assertEquals(StartEvent.class, events.get(0).getClass());
        assertTrue(gameInfo.hasDealerAction());
    }

    @Test
    public void testSimplePlay() throws Exception
    {
        GameInfo gameInfo = getGameInfo();
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatOne);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatTwo);

        assertEquals("8c 2h", Card.serialize(gameInfo.getHoleCards(seatOne)));
        assertEquals("4d 3c", Card.serialize(gameInfo.getHoleCards(seatTwo)));
        playerOne.betTo(50);
        playerTwo.call();
        dealer.collectCoins();
        assertEquals(100, gameInfo.getPotSize());
        dealer.flop();
        assertEquals("8h Jc 7h", Card.serialize(gameInfo.getBoardCards()));
        playerTwo.check();
        playerOne.check();
        assertEquals(100, gameInfo.getPotSize());
        dealer.collectCoins();
        dealer.turn();
        assertEquals("8h Jc 7h 6s", Card.serialize(gameInfo.getBoardCards()));
        playerTwo.check();
        playerOne.check();
        assertEquals(100, gameInfo.getPotSize());
        dealer.collectCoins();
        dealer.river();
        assertEquals("8h Jc 7h 6s Qd", Card.serialize(gameInfo.getBoardCards()));
        playerTwo.check();
        playerOne.betTo(100);
        playerTwo.fold();
        dealer.collectCoins();
        assertEquals(200, gameInfo.getPotSize());
        dealer.showDown();
    }

    @Test
    public void testHasActionTwoPlayers() throws Exception
    {
        GameInfo gameInfo = getGameInfo();
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatOne);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatTwo);

        assertEquals(seatOne, gameInfo.getOnButton().get());
        // at two heads up (two players) the dealer/on the button is smallblind
        assertFalse(gameInfo.isBigBlind(seatOne));
        assertTrue(gameInfo.isBigBlind(seatTwo));
        assertEquals(seatOne, gameInfo.getSeatHasAction().get());
        playerOne.betTo(50);
        assertEquals(seatTwo, gameInfo.getSeatHasAction().get());
        playerTwo.call();
        assertTrue(gameInfo.hasDealerAction());
        dealer.collectCoins();
        assertEquals(100, gameInfo.getPotSize());
        assertTrue(gameInfo.hasDealerAction());
        dealer.flop();
        assertEquals("8h Jc 7h", Card.serialize(gameInfo.getBoardCards()));
        assertEquals(seatTwo, gameInfo.getSeatHasAction().get());
        playerTwo.check();
        assertEquals(seatOne, gameInfo.getSeatHasAction().get());
        playerOne.check();
        assertEquals(100, gameInfo.getPotSize());
        assertTrue(gameInfo.hasDealerAction());
        dealer.collectCoins();
        assertTrue(gameInfo.hasDealerAction());
        dealer.turn();
        assertEquals("8h Jc 7h 6s", Card.serialize(gameInfo.getBoardCards()));
        assertEquals(seatTwo, gameInfo.getSeatHasAction().get());
        playerTwo.check();
        assertEquals(seatOne, gameInfo.getSeatHasAction().get());
        playerOne.check();
        assertEquals(100, gameInfo.getPotSize());
        assertTrue(gameInfo.hasDealerAction());
        dealer.collectCoins();
        assertTrue(gameInfo.hasDealerAction());
        dealer.river();
        assertEquals("8h Jc 7h 6s Qd", Card.serialize(gameInfo.getBoardCards()));
        assertEquals(seatTwo, gameInfo.getSeatHasAction().get());
        playerTwo.check();
        assertEquals(seatOne, gameInfo.getSeatHasAction().get());
        playerOne.betTo(100);
        assertEquals(seatTwo, gameInfo.getSeatHasAction().get());
        playerTwo.call();
        assertTrue(gameInfo.hasDealerAction());
        dealer.collectCoins();
        assertTrue(gameInfo.hasDealerAction());
        dealer.showDown();
    }

    @Test
    public void testHasActionThreePlayers() throws Exception
    {
        GameInfo gameInfo = getGameInfoPlayers3();
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatOne);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatTwo);
        PlayerActions playerThree = playerFactory.get(gameInfo, seatThree);

        assertEquals(seatOne, gameInfo.getOnButton().get());
        assertTrue(gameInfo.isSmallBlind(seatTwo));
        assertTrue(gameInfo.isBigBlind(seatThree));
        assertEquals(seatOne, gameInfo.getSeatHasAction().get());
        playerOne.betTo(50);
        assertEquals(seatTwo, gameInfo.getSeatHasAction().get());
        playerTwo.call();
        assertEquals(seatThree, gameInfo.getSeatHasAction().get());
        playerThree.call();
        assertTrue(gameInfo.hasDealerAction());
        dealer.collectCoins();
        assertEquals(150, gameInfo.getPotSize());
        assertTrue(gameInfo.hasDealerAction());
        dealer.flop();
        // after flop seat two starts
        assertEquals(seatTwo, gameInfo.getSeatHasAction().get());
        playerTwo.check();
        assertEquals(seatThree, gameInfo.getSeatHasAction().get());
        playerThree.allIn();
        assertEquals(seatOne, gameInfo.getSeatHasAction().get());
        playerOne.call();
        assertEquals(seatTwo, gameInfo.getSeatHasAction().get());
        playerTwo.fold();
        assertTrue(gameInfo.hasDealerAction());
        dealer.collectCoins();
        assertEquals(250, gameInfo.getPotSize());
        assertTrue(gameInfo.hasDealerAction());
        dealer.turn();
        // allin and fold
        assertTrue(gameInfo.hasDealerAction());
        dealer.river();
        assertTrue(gameInfo.hasDealerAction());
        dealer.showDown();
    }
}
