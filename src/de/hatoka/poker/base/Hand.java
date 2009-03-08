package de.hatoka.poker.base;

public class Hand 
implements Comparable<Object>
{
	
	public static final int STRAIGHT_FLUSH = 8;
	public static final int FOUR_OF_KIND   = 7;
	public static final int FULL_HOUSE     = 6;
	public static final int FLUSH          = 5;
	public static final int STRAIGHT       = 4;
	public static final int THREE_OF_KIND  = 3;
	public static final int TWO_PAIR       = 2;
	public static final int ONE_PAIR       = 1;
	public static final int HIGH_CARD      = 0;

	public static String possibleHand[] = { 
		"HighCard","OnePair","TwoPair","ThreeOfKind","Straight","Flush","FullHouse","FourOfKind","StraightFlush"
	};

	private Card[] cards;
	
	private int rank[] = {HIGH_CARD,0,0,0,0,0};
	private int flushColor = -1;
	int cDiffColor[] = {0,0,0,0,0,0,0,0,0,0,0,0,0}; // 13 different colors cards
	int cSameColor[] = {0,0,0,0,0,0,0,0,0,0,0,0,0}; // 13 flush colored cards
	
	public Hand(Card[] cards) {
		this.cards = cards;
	}
	
	public void recalc(Card[] cards) {
		this.cards = cards;
		rank[0] = defineRank();
		defineHighSteps();
	}
	void init () {
		for(int i=0;i<cDiffColor.length;i++) {
			cDiffColor[i] = 0;
			cSameColor[i] = 0;
		}

	}
	void defineHighSteps () {
		for(int j=2;j<6;j++) rank[j] = -1;
		if (rank[0] == Hand.STRAIGHT_FLUSH || rank[0] == Hand.STRAIGHT) {
			// rank1High is set at defineRank()
			return;
		}
		rank[1] = -1;
		if (Hand.FOUR_OF_KIND == rank[0]) {
			for(int i=0;i<cDiffColor.length;i++) {
				if (cDiffColor[i] > 0) {
					if (cDiffColor[i] == 4) {
						rank[1] = i;
					} else {
						rank[2] = i;
					}
				}
			}
		} else if (Hand.FULL_HOUSE == rank[0]) {
			for(int i=0;i<cDiffColor.length;i++) {
				if (cDiffColor[i] > 0) {
					if (cDiffColor[i] == 3) {
						// two 3 of kind
						if (rank[2] < rank[1]) {
							rank[2] = rank[1];
						}
						rank[1] = i;
						
					} else if (cDiffColor[i] == 2) {
						// two little pairs
						if (rank[3] < rank[2]) {
							rank[3] = rank[2]; 
						}
						rank[2] = i;
					} else { // diffentColor[i] == 1
						if (-1 == rank[3]) {
							rank[3] = i;
						}
					}
				}
			}
		} else if (Hand.THREE_OF_KIND == rank[0]) {
			for(int i=cDiffColor.length-1;i>=0;i--) {
				if (cDiffColor[i] > 0) {
					if (cDiffColor[i] == 3) {
						rank[1] = i;
					} else { // diffentColor[i] == 1
						for(int j=2;j<5;j++) {
							if(-1 == rank[j]) {
								rank[j] = i;
								break;
							}
						}
					}
				}
			}
		} else if (Hand.TWO_PAIR == rank[0]) {
			for(int i=cDiffColor.length-1;i>=0;i--) {
				if (cDiffColor[i] > 0) {
					if (cDiffColor[i] == 2) {
						if (-1 == rank[1]) {
							rank[1] = i;
						} else if (-1 == rank[2]) {
							rank[2] = i;
						} else if (-1 == rank[3]){
							rank[3] = i;
						}
					} else { // diffentColor[i] == 1
						if (-1 == rank[3]) {
							rank[3] = i;
						}
					}
				}
			}
		} else if (Hand.ONE_PAIR == rank[0]) {
			for(int i=cDiffColor.length-1;i>=0;i--) {
				if (cDiffColor[i] > 0) {
					if (cDiffColor[i] == 2) {
						if (-1 == rank[1]) {
							rank[1] = i;
						}
					} else { // diffentColor[i] == 1
						for(int j=2;j<5;j++) {
							if(-1 == rank[j]) {
								rank[j] = i;
								break;
							}
						}
					}
				}
			}
		} else if (Hand.HIGH_CARD == rank[0]) {
			for(int i=cDiffColor.length-1;i>=0;i--) {
				if (cDiffColor[i] > 0) {
					for(int j=1;j<6;j++) {
						if(-1 == rank[j]) {
							rank[j] = i;
							break;
						}
					}
				}
			}
		} else if (Hand.FLUSH == rank[0]) {
			for(int i=cSameColor.length-1;i>=0;i--) {
				if (cSameColor[i] > 0) {
					for(int j=1;j<6;j++) {
						if(-1 == rank[j]) {
							rank[j] = i;
							break;
						}
					}
				}
			}
		}		
	}

	int defineRank() {
		int ranks[] = {1,0,0,0,0,0,0,0,0};

		init();
		// count different cards
		for(int i=0;i<cards.length;i++) {
			cDiffColor[cards[i].getImage().getIndex()]++;
		}
		for(int i=0;i<cDiffColor.length;i++) {
			// four of kind
			if (cDiffColor[i] == 4) ranks[FOUR_OF_KIND]++;  // four of kind 
			if (cDiffColor[i] == 3) ranks[THREE_OF_KIND]++; // three of kind
			if (cDiffColor[i] == 2) ranks[ONE_PAIR]++; // pair
		}
		if (ranks[THREE_OF_KIND] > 0 && ranks[ONE_PAIR] > 0) ranks[FULL_HOUSE]++; // full house
		if (ranks[ONE_PAIR] > 1 ) ranks[TWO_PAIR]++; // two pairs
		// test straight, Ass can be also one
		if (cDiffColor[12]>0 && cDiffColor[0]>0 && cDiffColor[1]>0 && cDiffColor[2]>0 && cDiffColor[3]>0) {
			rank[1] = 3;
			ranks[STRAIGHT]++;
		}
		for(int i=0;i<cDiffColor.length-4;i++) {
			if (cDiffColor[i+0]>0 && cDiffColor[i+1]>0 && cDiffColor[i+2]>0 && cDiffColor[i+3]>0 && cDiffColor[i+4]>0) {
				rank[1] = i+4;
				ranks[STRAIGHT]++;
			}
		}	
		// test flush
		int colors[] = {0,0,0,0};
		for(int i=0;i<cards.length;i++) {
			colors[cards[i].getColor().getIndex()]++;
		}
		flushColor = -1;
		for(int i=0;i<colors.length;i++) {
			if (colors[i] > 4) {
				ranks[FLUSH]++;
				flushColor = i;
				// add only cards of flush color
				for(int j=0;j<cards.length;j++) {
					if (cards[j].getColor().getIndex() == flushColor) cSameColor[cards[j].getImage().getIndex()]++;
				}
			}
		}
		// test straight flush
		if (ranks[FLUSH] > 0 && ranks[STRAIGHT] > 0) {
			// Ace to 5
			if (cSameColor[12]>0 && cSameColor[0]>0 && cSameColor[1]>0 && cSameColor[2]>0 && cSameColor[3]>0) {
				rank[1] = 3;
				ranks[STRAIGHT_FLUSH]++;
			}
			// Ace to 10 until 6 to 2
			for(int i=0;i<cSameColor.length-4;i++) {
				if (cSameColor[i+0]>0 && cSameColor[i+1]>0 && cSameColor[i+2]>0 && cSameColor[i+3]>0 && cSameColor[i+4]>0) {
					rank[1] = i+4;
					ranks[STRAIGHT_FLUSH]++;
				}
			}
		}
		// find highest rank
		for(int i=STRAIGHT_FLUSH;i>HIGH_CARD;i--) {
			if (ranks[i]>0) return i;
		}
		return HIGH_CARD;
	}

	public Image getRank(int pos) {
		if (-1 == rank[pos]) {
			return null;
		}
		return Image.GetImage(rank[pos]);
	}
	public int getRank() {
		return rank[0];
	}
	
	public int getRankOfCards (Card[] cards) {
		recalc(cards);
		return rank[0];
	}
	
	public int getHandIndex() {
		int ext = 13;
		return ((((rank[0] * ext + rank[1]) * ext + rank[2]) * ext + rank[3]) * ext + rank[4]) * ext + rank[5];
	}
	
	public int compareTo(Object aHand) throws ClassCastException {
	    if (!(aHand instanceof Hand))
	      throw new ClassCastException("A Hand object expected.");
	    Hand hand = (Hand) aHand;
	    return this.getHandIndex() - hand.getHandIndex();    
	}
	
	public String getRankName() {
		return possibleHand[rank[0]];
	}
	
	public String getRankString() {
		StringBuilder s = new StringBuilder();
		for(int i=1;i<6;i++) {
			if (0 < rank[i]) {
				s.append(getRank(i).getName());
			}
		}
		return s.toString();
	}
	
	public String toString () {
		return getRankName().concat(" "  + getRankString());
	}

		
}
