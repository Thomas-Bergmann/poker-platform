package de.hatoka.poker.bot.remote.client;

import de.hatoka.poker.remote.PlayerGameActionRO;

public interface PlayerActions
{
    /**
     * checks - mean no additional coins
     */
    PlayerGameActionRO check();
    
    /**
     * call - requested amount of coins to have same coins in play as player with highest amount
     * @return coins moved to table
     */
    PlayerGameActionRO call();
    
    /**
     * Player bets additional coins (in case of paid blinds or earlier bets, these coins will not be added) 
     * @param coins
     */
    PlayerGameActionRO betTo(int coins);

    /**
     * Player bets additional coins (in case of paid blinds or earlier bets, these coins will not be added) 
     * @param coins
     */
    default PlayerGameActionRO raiseTo(int coins)
    {
        return betTo(coins);
    }

    /**
     * Player raises or call with all coins on seat
     */
    PlayerGameActionRO allIn();

    /**
     * Player gives up
     */
    PlayerGameActionRO fold();
    
    /**
     * Player submits action to server
     * @param action
     */
    void submit(PlayerGameActionRO action);
}
