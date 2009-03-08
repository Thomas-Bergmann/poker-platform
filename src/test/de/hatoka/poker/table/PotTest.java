package test.de.hatoka.poker.table;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import de.hatoka.poker.base.Player;
import de.hatoka.poker.table.Pot;
import de.hatoka.poker.table.Seat;

public class PotTest {

    @Test
	public void testnoSidePot() {
        Seat thomas = new Seat((new Player("thomas", 1500)), 500);
	    Seat katrin = new Seat((new Player("katrin", 1000)),1500);
	    ArrayList<Seat> seats = new ArrayList<Seat>();
	    seats.add(thomas);
	    seats.add(katrin);
	    Pot pot = new Pot(seats);
	    thomas.setCoins(20);
	    katrin.setCoins(40);
	    thomas.setCoins(20);
	    pot.collectCoins();
	    assertEquals(80, pot.getCoinCount());
	}
    
    @Test
	public void testCreateSidePot() {
        Seat thomas = new Seat((new Player("thomas", 1500)), 500);
	    Seat katrin = new Seat((new Player("katrin", 1000)),1500);
	    Seat ben    = new Seat((new Player("ben",    500 )),1500);
	    Seat arni   = new Seat((new Player("arni",   500 )),1500);
	    ArrayList<Seat> seats = new ArrayList<Seat>();
	    seats.add(thomas);
	    seats.add(katrin);
	    seats.add(ben);
	    seats.add(arni);
	    Pot pot = new Pot(seats);
	    thomas.setCoins(20);
	    katrin.setCoins(40);
	    ben.setCoins(60);
	    arni.setCoins(60);
	    pot.collectCoins();
	    assertEquals(180, pot.getCoinCount());
	}

	@Test
	public void testCreateSidePotWithTakeBack() {
        Seat thomas = new Seat((new Player("thomas", 1500)), 500);
	    Seat katrin = new Seat((new Player("katrin", 1000)),1500);
	    ArrayList<Seat> seats = new ArrayList<Seat>();
	    seats.add(thomas);
	    seats.add(katrin);
	    Pot pot = new Pot(seats);
	    thomas.setCoins(20);
	    katrin.setCoins(40);
	    pot.collectCoins();
	    assertEquals(40, pot.getCoinCount()); // 20 goes back to katrin
	}

    @Test
	public void testFoldSeat() {
        Seat thomas = new Seat((new Player("thomas", 1500)), 500);
	    Seat katrin = new Seat((new Player("katrin", 1000)),1500);
	    Seat ben    = new Seat((new Player("ben",    500 )),1500);
	    ArrayList<Seat> seats = new ArrayList<Seat>();
	    seats.add(thomas);
	    seats.add(katrin);
	    seats.add(ben);
	    Pot pot = new Pot(seats);
	    thomas.setCoins(20);
	    katrin.setCoins(20);
	    ben.setCoins(20);
	    pot.collectCoins();
	    thomas.setCoins(40);
	    pot.foldSeat(katrin);
	    pot.collectCoins();	    
	    assertEquals(60, pot.getCoinCount());
	    thomas.setCoins(20);
	    ben.setCoins(40);
	    pot.foldSeat(thomas);
	    pot.collectCoins();	
	    assertEquals(100, pot.getCoinCount());
	}


}
