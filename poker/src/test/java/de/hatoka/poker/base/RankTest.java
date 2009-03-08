package de.hatoka.poker.base;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RankTest {

	@Test
	public void testCompare() {
		assertTrue(0 < Rank.FLUSH.compareTo(Rank.STRAIGHT));
		assertTrue(0 < Rank.FULL_HOUSE.compareTo(Rank.FLUSH));
	}
}
