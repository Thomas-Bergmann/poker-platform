package de.hatoka.poker.table;

import java.util.ArrayList;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.base.Player;

public class Seat
	extends Object
{
	private int amountOfCoinsOnSeat = 0; // coins of player on seat
	private int amountOfCoinsInPlay = 0;  // coins of player on table
	private Player player = null;
	private boolean sittingOut = true;  // not on table
	private boolean hasFolded = false;  // has folded
	private boolean isAllIn = false;    // in play but all in, not further game
	ArrayList<Card> cards = null;

	/*
	 *  Constructor to create new seat
	 */
	public Seat () {
		this(null, 0);
	}
	
	public Seat (Player player, int coins) {
		if(null != player) {
			join(player,coins);
		}
		cards = new ArrayList<Card>();
	}

	public boolean join (Player player, int coins) {
		int possibleCoins = player.substractCoins(coins);
		if (0 == possibleCoins)
			return false;
		amountOfCoinsOnSeat = possibleCoins;
		amountOfCoinsInPlay = 0;
		this.player = player;
		sittingOut = false;
		return true;
	}
	
	public void leave () {
		player.addCoins(amountOfCoinsOnSeat);
		amountOfCoinsOnSeat = 0;
		player = null;
		sittingOut = true;
	}

	/*
	 *  coin functions for pot functionality
	 */
	public int getAmountOfCoinsInPlay () {
		return amountOfCoinsInPlay;
	}
	public void setAmountOfCoinsInPlay (int coins) {
		amountOfCoinsInPlay = coins;
	}
	
	public int getAmountOfCoinsOnSeat () {
		return amountOfCoinsOnSeat;
	}
	
	public void setAmountOfCoinsOnSeat (int coins) {
		amountOfCoinsOnSeat = coins;
	}

	public void takeBack(int coins) {
		amountOfCoinsOnSeat += coins;
		amountOfCoinsInPlay -= coins; 
	}
		
	public void setCoins (int coins) {
		if (amountOfCoinsOnSeat <= coins) {
			isAllIn = true;
			coins = amountOfCoinsOnSeat;
		}
		amountOfCoinsInPlay += coins; 
		amountOfCoinsOnSeat -= coins;
	}

	public void allIn() {
		isAllIn = true;
		amountOfCoinsInPlay += amountOfCoinsOnSeat;
		amountOfCoinsOnSeat = 0;
	}
	
	/*
	 *  player functions for table functionality
	 */
	public Player getPlayer() {
		return player;
	}

	public boolean isFree() {
		return player == null ? true : false;
	}

	public boolean isSittingOut() {
		return sittingOut;
	}

	public void setSittingOut(boolean sittingOut) {
		this.sittingOut = sittingOut;
	}

	public boolean isAllIn() {
		return isAllIn;
	}

	public boolean hasFolded() {
		return hasFolded;
	}

	public void fold() {
		hasFolded = true;
	}
	
	public void call (int coins) {
		setCoins(coins - amountOfCoinsInPlay);
	}

	public void newGame() {
		isAllIn = false;
		sittingOut = amountOfCoinsOnSeat == 0;
	}
	
	

	public void win (int coins) {
		amountOfCoinsOnSeat += coins;
		amountOfCoinsInPlay = 0;
	}
	public void lost () {
		amountOfCoinsInPlay = 0;
	}

	public String toString () {
		if (isFree())
			return "free";
		return this.player.getNickName() + " (Coins " + 
			(new Integer(amountOfCoinsInPlay)).toString() + "/" +
			(new Integer(amountOfCoinsOnSeat)).toString() + ")";
	}
}
