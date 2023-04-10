package de.hatoka.poker.table.internal.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.game.DealerInGame;
import de.hatoka.poker.table.capi.event.game.PlayerFactory;
import de.hatoka.poker.table.capi.event.history.lifecycle.StartEvent;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class PlayerGameInfoTest
{
    private static final DealerResourceLoader LOADER = new DealerResourceLoader();
    private static final SeatRef seatOneRef = SeatRef.globalRef("seat:0@table:DealerInfoTest");
    private static final SeatRef seatTwoRef = SeatRef.globalRef("seat:1@table:DealerInfoTest");

    @Autowired
    private PlayerFactory playerFactory;

    private GameInfo getGameInfo() throws URISyntaxException, IOException
    {
        return LOADER.load(getResourceURI("PlayerGameInfoTest_StartEvent2.csv"));
    }

    private URI getResourceURI(String resourcePath) throws URISyntaxException
    {
        return getClass().getResource(resourcePath).toURI();
    }

    @Test
    @Timeout(10)
    public void testDontShowDeck() throws Exception
    {
        GameInfo gameInfo = getGameInfo();
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();

        PlayerGameInfo playerInfoOne = playerFactory.getInfo(gameInfo, seatOneRef);
        // deck not visible
        assertTrue(playerInfoOne.getFirstEvent(StartEvent.class).isEmpty());
    }

    @Test
    @Timeout(10)
    public void testShowdownAfterCallWithoutLastAggressor() throws Exception
    {
        GameInfo gameInfo = getGameInfo();
        
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatOneRef);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatTwoRef);
        PlayerGameInfo playerInfoOne = playerFactory.getInfo(gameInfo, seatOneRef);
        PlayerGameInfo playerInfoTwo = playerFactory.getInfo(gameInfo, seatTwoRef);

        assertEquals("8c 2h", Card.serialize(playerInfoOne.getHoleCards()));
        assertEquals("4d 3c", Card.serialize(playerInfoTwo.getHoleCards()));
        playerOne.betTo(50);
        playerTwo.call();
        dealer.flop();
        assertEquals("8h Jc 7h", Card.serialize(playerInfoOne.getBoardCards()));
        playerTwo.check();
        playerOne.betTo(100);
        playerTwo.call();
        dealer.turn();
        dealer.river();
        dealer.showDown();

        // player two can mucks his hand, cause he is the second player in the round
        // player one is not aggressor, but not small blind
        assertEquals("8c 8h Qd Jc 7h", Card.serialize(playerInfoOne.getCardsOfHand(seatOneRef)));
        assertEquals("", Card.serialize(playerInfoOne.getCardsOfHand(seatTwoRef)));
        assertEquals("8c 8h Qd Jc 7h", Card.serialize(playerInfoTwo.getCardsOfHand(seatOneRef)));
        assertEquals("Qd Jc 8h 7h 6s", Card.serialize(playerInfoTwo.getCardsOfHand(seatTwoRef)));
    }

    @Test
    @Timeout(10)
    public void testShowdownAfterCallLastAggressor() throws Exception
    {
        GameInfo gameInfo = getGameInfo();
        
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatOneRef);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatTwoRef);
        PlayerGameInfo playerInfoOne = playerFactory.getInfo(gameInfo, seatOneRef);
        PlayerGameInfo playerInfoTwo = playerFactory.getInfo(gameInfo, seatTwoRef);

        assertEquals("8c 2h", Card.serialize(playerInfoOne.getHoleCards()));
        assertEquals("4d 3c", Card.serialize(playerInfoTwo.getHoleCards()));
        playerOne.betTo(50);
        playerTwo.call();
        dealer.flop();
        assertEquals("8h Jc 7h", Card.serialize(playerInfoOne.getBoardCards()));
        playerTwo.check();
        playerOne.check();
        dealer.turn();
        playerTwo.check();
        playerOne.check();
        dealer.river();
        playerTwo.check();
        playerOne.betTo(100);
        playerTwo.call();
        dealer.showDown();

        // player two can mucks his hand, cause last aggressor (player one) shows cards first
        assertEquals("8c 8h Qd Jc 7h", Card.serialize(playerInfoOne.getCardsOfHand(seatOneRef)));
        assertEquals("", Card.serialize(playerInfoOne.getCardsOfHand(seatTwoRef)));
        assertEquals("8c 8h Qd Jc 7h", Card.serialize(playerInfoTwo.getCardsOfHand(seatOneRef)));
        assertEquals("Qd Jc 8h 7h 6s", Card.serialize(playerInfoTwo.getCardsOfHand(seatTwoRef)));
    }

    @Test
    @Timeout(10)
    public void testShowCardsAfterAllIn() throws Exception
    {
        GameInfo gameInfo = getGameInfo();
        
        DealerInGame dealer = new DealerInGameImpl(gameInfo);
        dealer.doWhatEverYouNeed();
        PlayerActions playerOne = playerFactory.get(gameInfo, seatOneRef);
        PlayerActions playerTwo = playerFactory.get(gameInfo, seatTwoRef);
        PlayerGameInfo playerInfoOne = playerFactory.getInfo(gameInfo, seatOneRef);
        PlayerGameInfo playerInfoTwo = playerFactory.getInfo(gameInfo, seatTwoRef);

        assertEquals("8c 2h", Card.serialize(playerInfoOne.getHands().get(seatOneRef).getHoleCards()));
        playerOne.betTo(50);
        playerTwo.call();
        dealer.flop();
        playerTwo.allIn();
        playerOne.allIn();
        dealer.turn();
        dealer.river();
        dealer.showDown();
        assertEquals("8c 8h Qd Jc 7h", Card.serialize(playerInfoOne.getCardsOfHand(seatOneRef)));
        assertEquals("8c 8h Qd Jc 7h", Card.serialize(playerInfoTwo.getCardsOfHand(seatOneRef)));
        assertEquals("Qd Jc 8h 7h 6s", Card.serialize(playerInfoOne.getCardsOfHand(seatTwoRef)));
        assertEquals("Qd Jc 8h 7h 6s", Card.serialize(playerInfoTwo.getCardsOfHand(seatTwoRef)));
        assertEquals("8c 2h", Card.serialize(playerInfoTwo.getHands().get(seatOneRef).getHoleCards()));
    }
}
