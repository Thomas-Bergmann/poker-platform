package de.hatoka.poker.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ColorTest {
	
	Color entry = Color.DIAMONDS;

	@Test
	public void testGetColorInt() {
		assertEquals(Color.DIAMONDS, entry);
	}

	@Test
	public void testGetColorString() {
		Color entry = Color.valueViaAbbreviation("c");
		assertEquals(Color.CLUBS, entry);
	}

	@Test
	public void testGetIndex() {
		assertEquals(Color.DIAMONDS, entry);
	}

	@Test
	public void testGetName() {
		assertEquals("d", entry.getAbbreviation());
	}

	@Test
	public void testGetDescription() {
		assertEquals("Diamonds", entry.getDescription());
	}

	@Test
	public void testToString() {
		assertEquals("d", entry.toString());
	}

}
