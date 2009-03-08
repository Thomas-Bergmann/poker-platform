package test.de.hatoka.poker.table;

import static org.junit.Assert.*;

import org.junit.Test;

import de.hatoka.poker.base.Player;
import de.hatoka.poker.table.Seat;

public class SeatTest {

	@Test
	public void testSeat() {
	    Seat seat = new Seat();
	    assertEquals(0, seat.getAmountOfCoinsOnSeat());
	    assertEquals(null, seat.getPlayer());
	}

	@Test
	public void testSeatPlayerInt() {
	    Player thomas = new Player("thomas", 1500);
	    Seat seat = new Seat(thomas, 500);
	    assertEquals(500, seat.getAmountOfCoinsOnSeat());
	    assertEquals("thomas", seat.getPlayer().getNickName());
	    assertEquals(1000, seat.getPlayer().getAmountOfCoins());
	}

	@Test
	public void testGame() {
	    Player thomas = new Player("thomas", 1500);
	    Player katrin = new Player("katrin", 1500);
	    Seat seatThomas = new Seat(thomas,  500);
	    Seat seatKatrin = new Seat(katrin, 1000);
	    
	    seatThomas.setCoins(20);
	    seatKatrin.setCoins(40);
	    seatThomas.setCoins(20);
	    // same amount in pot
	    assertEquals(seatThomas.getAmountOfCoinsInPlay(), seatKatrin.getAmountOfCoinsInPlay());
	    	
	    // collect pot
	    int coins = seatThomas.getAmountOfCoinsInPlay() + seatKatrin.getAmountOfCoinsInPlay();
	    seatKatrin.win(coins);
	    seatThomas.lost();
	    
	    assertEquals(1040, seatKatrin.getAmountOfCoinsOnSeat());
	    assertEquals(460, seatThomas.getAmountOfCoinsOnSeat());
	}

	@Test
	public void testAllInFull() {
	    Player thomas = new Player("thomas", 1500);
	    Player katrin = new Player("katrin", 1500);
	    Seat seatThomas = new Seat(thomas,  500);
	    Seat seatKatrin = new Seat(katrin, 1000);
	    
	    seatThomas.setCoins(20);
	    seatKatrin.setCoins(40);
	    seatThomas.allIn();
	    assertTrue(seatThomas.isAllIn());
	    seatKatrin.call(seatThomas.getAmountOfCoinsInPlay());
	    assertFalse(seatKatrin.isAllIn());

	    // collect pot
	    int coins = seatThomas.getAmountOfCoinsInPlay() + seatKatrin.getAmountOfCoinsInPlay();
	    seatKatrin.win(coins);
	    seatThomas.lost();
	    
	    assertEquals(1500, seatKatrin.getAmountOfCoinsOnSeat());
	    assertEquals(0,    seatThomas.getAmountOfCoinsOnSeat());
	}
	
	@Test
	public void testAllInPart() {
	    Player thomas = new Player("thomas", 3500);
	    Player katrin = new Player("katrin", 1500);
	    Seat seatThomas = new Seat(thomas, 2500);
	    Seat seatKatrin = new Seat(katrin, 1000);
	    
	    seatThomas.setCoins(20);
	    seatKatrin.setCoins(40);
	    seatThomas.allIn();
	    assertTrue(seatThomas.isAllIn());
	    seatKatrin.call(seatThomas.getAmountOfCoinsInPlay());
	    assertTrue(seatKatrin.isAllIn());
	    seatThomas.takeBack(seatThomas.getAmountOfCoinsInPlay() - seatKatrin.getAmountOfCoinsInPlay());
	    
	    // collect pot
	    int coins = seatThomas.getAmountOfCoinsInPlay() + seatKatrin.getAmountOfCoinsInPlay();
	    
	    seatKatrin.win(coins);
	    seatThomas.lost();
	    
	    assertEquals(2000, seatKatrin.getAmountOfCoinsOnSeat());
	    assertEquals(1500, seatThomas.getAmountOfCoinsOnSeat());
	}
}
