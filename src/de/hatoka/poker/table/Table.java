package de.hatoka.poker.table;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Deck;
import de.hatoka.poker.base.Hand;
import de.hatoka.poker.base.Player;

public class Table {

	private int identifier = 0;
	public static int lastIndentifier = 0;
	private static int countSeats = 3;
	private Seat seats[] = new Seat[countSeats];
	private static int countBoardCards = 5;
	private Card boardCards[] = new Card[countBoardCards];
	private Deck deck = null;
	public int startMaxCoins = 500;
	
	public Table () {
		identifier = ++lastIndentifier;
		deck = new Deck();
		deck.shuffle();
		for (int i=0;i<countSeats;i++) {
			seats[i] = new Seat();
		}
	}
	
	public boolean join (Player player, int seatNumber, int coins) {
		Seat seat = seats[seatNumber];
		if (! seat.isFree()) return false;
		for(int i=0;i<countSeats;i++) {
			Seat usedSeat = seats[i];
			if (!usedSeat.isFree() && usedSeat.getPlayer() == player) {
				return false;
			}
		}		
		return seat.join(player, coins);
	}
	
	public void givePlayerCards () {
		for(int i=0;i<countSeats;i++) {
			if (!seats[i].isFree())
				seats[i].setCards(deck.getNextCard(), deck.getNextCard());
		}
	}

	public void giveFlopCards () {
		for(int i=0;i<3;i++) {
			boardCards[i] = deck.getNextCard();
		}
	}
	public void giveTurnCard () {
		boardCards[3] = deck.getNextCard();
	}
	public void giveRiverCard () {
		boardCards[4] = deck.getNextCard();
	}


	public int getValueOfSeat(Seat seat) {
		if(seat.sittingOut) return -1;
		Card seatCards[] = seat.getCards();
		Card cards[] = new Card[boardCards.length + seatCards.length];
		for(int i=0;i<boardCards.length;i++) {
			cards[i]=boardCards[i];
		}
		for(int i=0;i<seatCards.length;i++) {
			cards[boardCards.length + i]=seatCards[i];
		}
		int rank = Hand.getRankOfCards(cards);
		return rank;
	}
	
	public String toString() {
		String boardString = "Board:";
		for(int i=0;i<countBoardCards;i++) {
			if(boardCards[i] != null)
				boardString += " " + boardCards[i].toString();
		}
		String seatString = "";
		for(int i=0;i<countSeats;i++) {
			seatString += "\n" + seats[i].toString() 
				+ "(Value: " + new Integer(getValueOfSeat(seats[i])).toString() + ")";
		}
		return "Table: "
			.concat(new Integer(identifier).toString()).concat("\n")
			.concat(boardString)
			.concat(seatString).concat("\n")
		    .concat(deck.toString()).concat("\n");
	}
	
	public static void main(String[] args) {
        System.out.println("Hello Poker-Fan.");
        Table table = new Table();
        Player thomas = new Player("thomas", 1500);
        Player katrin = new Player("katrin", 1500);
        table.join(thomas, 0, 500);
        table.join(katrin, 1, 500);
        table.join(katrin, 2, 500);
        table.givePlayerCards();
        table.giveFlopCards();
        table.giveTurnCard();
        table.giveRiverCard();
        System.out.println(table.toString());
	}
	
}
