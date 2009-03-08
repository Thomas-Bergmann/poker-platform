package test.de.hatoka.poker.table;

import static org.junit.Assert.*;

import org.junit.Test;

import de.hatoka.poker.base.Player;

public class PlayerTest {

	@Test
	public void testPlayerString() {
        Player player = new Player("Thomas");
        assertEquals("Thomas", player.getNickName());
        Player player2 = new Player("Katrin");
        assertEquals("Katrin", player2.getNickName());
        assertEquals(0, player.getAmountOfCoins());
	}

	@Test
	public void testPlayerStringInt() {
        Player player = new Player("Thomas", 100);
        assertEquals("Thomas", player.getNickName());
        assertEquals(100, player.getAmountOfCoins());
	}

	@Test
	public void testGetNickName() {
		String nickName = "Thomas";
        Player player = new Player(nickName);
        assertEquals("Thomas", player.getNickName());
        assertEquals(nickName, player.getNickName());
        nickName = "Katrin";
        assertNotSame(nickName, player.getNickName());
	}

	@Test
	public void testGetAmountOfCoins() {
        Player player = new Player("Thomas", 200);
        assertEquals(200, player.getAmountOfCoins());
	}

	@Test
	public void testSubstractCoins() {
        Player player = new Player("Thomas", 200);
        assertEquals(50, player.substractCoins(50));
        assertEquals(150, player.getAmountOfCoins());
        assertEquals(150, player.substractCoins(250));
        assertEquals(0, player.getAmountOfCoins());
	}

	@Test
	public void testAddCoins() {
        Player player = new Player("Thomas", 200);
        player.addCoins(50);
        assertEquals(250, player.getAmountOfCoins());
	}

	@Test
	public void testCompareTo() {
		Player player = new Player("Thomas");
		Player player2 = new Player("Katrin", 200);
		assertEquals(0,player2.compareTo(player2));
		assertEquals(200,player2.compareTo(player));
		assertEquals(-200,player.compareTo(player2));
	}

}
