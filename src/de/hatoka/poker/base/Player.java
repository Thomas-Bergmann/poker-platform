package de.hatoka.poker.base;

public class Player
	extends Object
	implements Comparable<Object> 
{
	private int amountOfCoins = 0;
	private String nickName = null;

	public Player (String nickName) {
		this.nickName = nickName;
	}
	public Player (String nickName, int initialCoins) {
		this.nickName = nickName;
		this.amountOfCoins = initialCoins;
	}
	
	public String getNickName () {
		return this.nickName;
	}
	
	public int getAmountOfCoins () {
		return this.amountOfCoins;
	}
	
	public int substractCoins (int coins) {
		int payOut = coins;
		if (amountOfCoins < coins)  {
			payOut = amountOfCoins;
		}
		amountOfCoins -= payOut;
		return payOut;
	}
	
	public void addCoins (int coins) {
		this.amountOfCoins += coins;
		return;
	}

	public int compareTo(Object aPlayer) throws ClassCastException {
	    if (!(aPlayer instanceof Player))
	      throw new ClassCastException("A Player object expected.");
	    Player player = (Player) aPlayer;
	    return (this.amountOfCoins - player.amountOfCoins);    
	}
	
	public String toString () {
		Integer amount = new Integer(this.amountOfCoins);
		return nickName + ": " + amount.toString();
	}

}
