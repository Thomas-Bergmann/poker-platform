package de.hatoka.poker.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class PotTest {

	@Test
	public void testNoSidePot() {
	    Map<String, Integer> inplay = new HashMap<>();
	    inplay.put("one", 40);
	    inplay.put("two", 40);

        Pot pot = Pot.generate();
		pot.addToPot(inplay);
		assertEquals(80, pot.getCoinCount());
	}

	@Test
	public void testCreateSidePot() {
        Map<String, Integer> inplay = new HashMap<>();
        inplay.put("one", 20);
        inplay.put("two", 40);
        inplay.put("three", 60);
        inplay.put("four", 60);

        Pot pot = Pot.generate();
        pot.addToPot(inplay);
		assertEquals(180, pot.getCoinCount());

		List<String> winners = Arrays.asList("one", "three");
		Map<String, Integer> payout = pot.showdownWithoutSplitPot(winners);
        assertEquals(80, payout.get("one"));
        assertEquals(100, payout.get("three"));
	}

    @Test
    public void testSplitPot() {
        Map<String, Integer> inplay = new HashMap<>();
        inplay.put("one", 20);
        inplay.put("two", 40);
        inplay.put("three", 60);
        inplay.put("four", 60);

        Pot pot = Pot.generate();
        pot.addToPot(inplay);
        assertEquals(180, pot.getCoinCount());

        List<String> winners = Arrays.asList("one", "three");
        Map<String, Integer> payout = pot.showdown(Arrays.asList(winners));
        assertEquals(40, payout.get("one")); // 20*4/2
        assertEquals(140, payout.get("three"));
    }
    
    @Test
    public void testPotSerialization() {
        Map<String, Integer> inplay = new HashMap<>();
        inplay.put("one", 20);
        inplay.put("two", 40);
        inplay.put("three", 60);
        inplay.put("four", 60);

        Pot pot = Pot.generate();
        pot.addToPot(inplay);
        String asString = pot.toString();
        Pot newPot = Pot.deserialize(asString);
        assertEquals(pot, newPot);
    }

    @Test
    public void testCoinsPerPlayer() {
        Map<String, Integer> inplay = new HashMap<>();
        inplay.put("one", 20);
        inplay.put("two", 40);
        inplay.put("three", 60);
        inplay.put("four", 60);

        Pot pot = Pot.generate();
        pot.addToPot(inplay);
        Map<String, Integer> coinsOfPlayer = pot.getCoinsOfPlayer();
        assertEquals(20, coinsOfPlayer.get("one"));
        assertEquals(40, coinsOfPlayer.get("two"));
        assertEquals(60, coinsOfPlayer.get("three"));
        assertEquals(60, coinsOfPlayer.get("four"));
    }
}
