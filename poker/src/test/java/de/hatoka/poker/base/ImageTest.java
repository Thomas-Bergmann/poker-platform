package de.hatoka.poker.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ImageTest {

	Image entry = Image.valueViaAbbreviation("A");

	@Test
	public void testGetImageInt() {
		Image entry = Image.THREE;
		assertEquals(Image.THREE, entry);
	}

	@Test
	public void testGetImageString() {
		Image entry = Image.valueViaAbbreviation("2");
		assertEquals(Image.TWO, entry);
	}

	@Test
	public void testGetIndex() {
		assertEquals(Image.ACE, entry);
	}

	@Test
	public void testGetName() {
		assertEquals("A", entry.getAbbreviation());
	}

	@Test
	public void testGetDescription() {
		assertEquals("Ace", entry.getDescription());
	}

	@Test
	public void testToString() {
		assertEquals("A", entry.toString());
	}
}
