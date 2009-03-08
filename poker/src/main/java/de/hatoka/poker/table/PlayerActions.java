package de.hatoka.poker.table;

public interface PlayerActions
{
    /**
     * checks - mean no additional coins
     */
    void check();
    
    /**
     * call - requested amount of coins to have same coins in play as player with highest amount
     * @return coins moved to table
     */
    void call();
    
    /**
     * Player bets additional coins (in case of paid blinds or earlier bets, these coins will not be added) 
     * @param coins
     */
    void betTo(int coins);

    /**
     * Player bets additional coins (in case of paid blinds or earlier bets, these coins will not be added) 
     * @param coins
     */
    default void raiseTo(int coins)
    {
        betTo(coins);
    }

    /**
     * Player raises or call with all coins on seat
     */
    void allIn();

    /**
     * Player gives up
     */
    void fold();
}
