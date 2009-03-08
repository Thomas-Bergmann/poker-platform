package de.hatoka.poker.base;

import java.util.Random;

public class Deck 
	extends Object 
{
	protected Card cards[] = new Card[Color.COUNT * Image.COUNT]; 
	protected static Random generator = null;
	protected int countShuffle = 123456; 
	protected int indexNextCard = -1;
	
	private void initDeck() {
		int index = 0;
		for (int indexColor=0;indexColor<Color.COUNT;indexColor++) {
			Color c = Color.GetColor(indexColor);
			for (int indexImage=0;indexImage<Image.COUNT;indexImage++) {
				cards[index++] = new Card(Image.GetImage(indexImage), c);
			}
		}
		countShuffle = cards.length * cards.length + generator.nextInt(cards.length);
		shuffle();
	}
	public Deck () {
		generator = new Random();
		initDeck();
	}

	public Deck (long seed) {
		generator = new Random(seed);
		initDeck();
	}
	
	protected Card getCard (int position) {
		return cards[position];
	}
	
	public Card getNextCard () {
		if (indexNextCard == cards.length) {
			return null;
		}
		return getCard(indexNextCard++);
	}
	
	public int getCountCards () {
		return cards.length;
	}
	
	public int getOutCards () {
		return indexNextCard;
	}
	
	public void shuffle () {
		indexNextCard = 0;
		for (int index=0;index<countShuffle;index++) {
			int firstIndex = generator.nextInt(cards.length);
			int secondIndex = generator.nextInt(cards.length);
			Card firstCard = cards[firstIndex]; 
			Card secondCard = cards[secondIndex];
			cards[firstIndex] = secondCard;
			cards[secondIndex] = firstCard;
		}
		return;
	}

	public String toString () {
		if (-1 == indexNextCard ) {
			return "Deck is sorted.";
		}
		if (0 == indexNextCard ) {
			return "Deck is shuffled.";
		}
		StringBuilder out = new StringBuilder();
		for (int index=0;index<indexNextCard;index++) {
			if (0 < index) {
				out.append(" ");
			}
			out = out.append(cards[index].toString());
		}
		return out.toString();
	}
}
