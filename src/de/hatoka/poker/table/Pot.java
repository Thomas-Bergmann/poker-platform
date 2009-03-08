package de.hatoka.poker.table;

import java.util.ArrayList;

public class Pot
extends Object
{
	private ArrayList<Seat> seats;
	private int amountCoins = 0;  // coins which can seats win, if != 0 is fix sitepot, getAmountOfCoinsInPlay is reduced
	private Pot sidePot = null;  // next side pot, the side pot includes more seats then the current pot
	private Boolean isMainPot = true;
	
	public Pot (ArrayList<Seat> seats) {
		this(seats,0, null, true);
	}
	
	public Pot (ArrayList<Seat> seats, int coins, Pot sidePot, Boolean isMainPot) {
		this.seats = seats;
		amountCoins = coins;
		this.sidePot = sidePot;
		this.isMainPot = isMainPot;
	}
	
	public Pot getSidePot() {
		return sidePot;
	}
	
	public void createSidePot(int coins) {
		ArrayList<Seat> newMainPot = new ArrayList<Seat>();
		for(int i=0;i<seats.size();i++) {
			Seat current = seats.get(i);
			if (coins < current.getAmountOfCoinsInPlay()) newMainPot.add(current);
		}
		if (1<newMainPot.size()) {
			for(int i=0;i<seats.size();i++) {
				Seat current = seats.get(i);
				current.setAmountOfCoinsInPlay(current.getAmountOfCoinsInPlay() - coins);
			}
			sidePot = new Pot(seats, coins * seats.size(), sidePot, false);
			seats = newMainPot;			
		} else {
			Seat current = newMainPot.get(0);
			current.takeBack(current.getAmountOfCoinsInPlay() - coins);
		}
	}

	public void collectCoins() {
		if (seats.isEmpty()) return;
		
		// find minCoins seat and max
		int minCoins = seats.get(0).getAmountOfCoinsInPlay(); // minimal coins of a seat
		int maxCoins = minCoins; // maximal coins of a seat
		int countMaxSeats = 1; // count of seats, which have maximal coins
		for(int i=0;i<seats.size();i++) {
			Seat current = (Seat) seats.get(i);
			int coins = current.getAmountOfCoinsInPlay();
			if (coins < minCoins) { minCoins = coins; }
			if (coins == maxCoins) { countMaxSeats++; }
			if (maxCoins < coins ) {
				countMaxSeats = 1;
				maxCoins = coins;
			}
		}
		if (minCoins != maxCoins) {
			createSidePot(minCoins);
			collectCoins();
		}
	}
	
	public int getCoinCount() {
		int count = amountCoins;
		if (isMainPot) {
			for(int i=0;i<seats.size();i++) {
				count += seats.get(i).getAmountOfCoinsInPlay();
			}
		}
		if(null != sidePot)
			count += sidePot.getCoinCount();
		return count;
	}
	
	public void foldSeat(Seat seat) {
		amountCoins += seat.getAmountOfCoinsInPlay();
		seat.setAmountOfCoinsInPlay(0);
		seats.remove(seat);
		if (null != sidePot) {
			sidePot.foldSeat(seat);
		}
	}
	
	public String toString () {
		StringBuilder str = new StringBuilder();
		if(0 < amountCoins)
			str.append("amountCoins:"+ amountCoins);
		for(int i=0;i<seats.size()-1;i++) {
			if (i>0) str.append(";");
			str.append(seats.get(i).toString());
		}
		str.append(";");
		if(null != sidePot)
			str.append(sidePot.toString());
		return str.toString();
	}
	
}
