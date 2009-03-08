package de.hatoka.poker.table.internal.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.base.Deck;
import de.hatoka.poker.player.capi.business.PlayerBORepository;
import de.hatoka.poker.player.capi.business.PlayerRef;
import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.poker.table.PlayerActions;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.capi.event.game.Dealer;
import de.hatoka.poker.table.capi.event.game.DealerFactory;
import de.hatoka.poker.table.capi.event.game.PlayerFactory;
import de.hatoka.user.capi.business.UserRef;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
class DealerOnTableImplTest
{
    private static final String NAME = "DealerOnTableImplTest";
    private static final TableRef TABLE_REF_ONE = TableRef.localRef(NAME);
    private static final UserRef USER_REF_ONE = UserRef.localRef("one");
    private static final UserRef USER_REF_TWO = UserRef.localRef("two");
    private static final UserRef USER_REF_THREE = UserRef.localRef("three");
    private static final PlayerRef PLAYER_REF_ONE = PlayerRef.humanRef(USER_REF_ONE);
    private static final PlayerRef PLAYER_REF_TWO = PlayerRef.humanRef(USER_REF_TWO);
    private static final PlayerRef PLAYER_REF_THREE = PlayerRef.humanRef(USER_REF_THREE);

    @Autowired
    private TableBORepository tableRepo;
    @Autowired
    private PlayerBORepository playerRepo;
    @Autowired
    private DealerFactory dealerFactory;
    @Autowired
    private PlayerFactory playerFactory;

    private SeatBORepository seatRepository;

    @BeforeEach
    public void createPlayer()
    {
        deleteRepo();
        playerRepo.createHumanPlayer(USER_REF_ONE);
        playerRepo.createHumanPlayer(USER_REF_TWO);
        playerRepo.createHumanPlayer(USER_REF_THREE);
    }

    @AfterEach
    public void deleteRepo()
    {
        tableRepo.clear();
        playerRepo.clear();
    }

    @Test
    @Timeout(5)
    public void testSimplePlay() throws IOException
    {
        Deck.initializeRandom(123456L);
        TableBO table = tableRepo.createTable(TABLE_REF_ONE, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT);
        seatRepository = table.getSeatRepository();
        SeatBO seatOne = seatRepository.createSeat();
        SeatBO seatTwo = seatRepository.createSeat();
        SeatBO seatThree= seatRepository.createSeat();
        seatOne.join(playerRepo.findPlayer(PLAYER_REF_ONE).get(), 500);
        seatTwo.join(playerRepo.findPlayer(PLAYER_REF_TWO).get(), 200);
        seatThree.join(playerRepo.findPlayer(PLAYER_REF_THREE).get(), 200);
        assertEquals(500, seatOne.getAmountOfCoinsOnSeat());
        Dealer dealer = dealerFactory.get(table);
        dealer.doWhatEverYouNeed();

        PlayerActions playerOne = playerFactory.get(seatOne);
        PlayerActions playerTwo = playerFactory.get(seatTwo);
        PlayerActions playerThree = playerFactory.get(seatThree);

        playerOne.betTo(50);
        playerTwo.call();
        playerThree.fold(); // lost ten coins big blind
        dealer.doWhatEverYouNeed();
        playerTwo.check();
        playerOne.allIn();
        playerTwo.fold(); // lost 50 coins
        dealer.doWhatEverYouNeed();
        assertFalse(dealer.getDealerInGame().get().canTransfer(), "the dealer should transfer");
        assertEquals(560, seatOne.getAmountOfCoinsOnSeat());
        assertEquals(150, seatTwo.getAmountOfCoinsOnSeat());
        assertEquals(190, seatThree.getAmountOfCoinsOnSeat());
    }

    @Test
    @Timeout(5)
    public void testBuyInDuringGame() throws IOException
    {
        Deck.initializeRandom(123456L);
        TableBO table = tableRepo.createTable(TABLE_REF_ONE, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT);
        seatRepository = table.getSeatRepository();
        SeatBO seatOne = seatRepository.createSeat();
        SeatBO seatTwo = seatRepository.createSeat();
        SeatBO seatThree= seatRepository.createSeat();
        seatOne.join(playerRepo.findPlayer(PLAYER_REF_ONE).get(), 500);
        seatTwo.join(playerRepo.findPlayer(PLAYER_REF_TWO).get(), 200);
        seatThree.join(playerRepo.findPlayer(PLAYER_REF_THREE).get(), 200);
        assertEquals(500, seatOne.getAmountOfCoinsOnSeat());
        Dealer dealer = dealerFactory.get(table);
        dealer.doWhatEverYouNeed();

        PlayerActions playerOne = playerFactory.get(seatOne);
        PlayerActions playerTwo = playerFactory.get(seatTwo);
        PlayerActions playerThree = playerFactory.get(seatThree);

        playerOne.betTo(50);
        playerTwo.call();
        playerThree.fold(); // lost ten coins big blind
        dealer.doWhatEverYouNeed();
        // buyin has now 500 on seat but 200 in game
        seatTwo.buyin(300);
        playerTwo.check();
        playerOne.allIn();
        playerTwo.fold(); // lost 50 coins
        dealer.doWhatEverYouNeed();
        assertFalse(dealer.getDealerInGame().get().canTransfer(), "the dealer should transfer");
        assertEquals(560, seatOne.getAmountOfCoinsOnSeat());
        // buyin has now 500, but has lost 50
        assertEquals(450, seatTwo.getAmountOfCoinsOnSeat());
        assertEquals(190, seatThree.getAmountOfCoinsOnSeat());
    }

    @Test
    @Timeout(5)
    public void testDontStartIfNoMoney() throws IOException
    {
        Deck.initializeRandom(123456L);
        TableBO table = tableRepo.createTable(TABLE_REF_ONE, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT);
        seatRepository = table.getSeatRepository();
        SeatBO seatOne = seatRepository.createSeat();
        SeatBO seatTwo = seatRepository.createSeat();
        seatOne.join(playerRepo.findPlayer(PLAYER_REF_ONE).get());
        seatTwo.join(playerRepo.findPlayer(PLAYER_REF_TWO).get());
        Dealer dealer = dealerFactory.get(table);
        assertFalse(dealer.getDealerOnTable().canInitialize());
        assertThrows(IllegalArgumentException.class, () -> seatOne.buyin(-200));
        assertThrows(IllegalArgumentException.class, () -> seatOne.buyin(5));
        assertThrows(IllegalArgumentException.class, () -> seatOne.buyin(table.getMaxBuyIn() + 1));
        seatOne.buyin(250);
        assertFalse(dealer.getDealerOnTable().canInitialize());
        assertThrows(IllegalArgumentException.class, () -> seatOne.buyin(table.getMaxBuyIn() - 250 + 1));
        seatTwo.buyin(250);
        assertTrue(dealer.getDealerOnTable().canInitialize());
    }
}
