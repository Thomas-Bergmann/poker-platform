package de.hatoka.poker.table.capi.business;

import de.hatoka.poker.player.capi.business.PlayerBO;

/**
 * The player is calling that methods (e.g. via controller)
 */
public interface SeatPlayerCommunication
{
    /**
     * Player uses that seat and is ready to play, and takes some coins to play from balance
     * @param player
     * @param coins number of coins from player.balance to table
     */
    default void join(PlayerBO player, int coins)
    {
        join(player);
        if (coins > 0)
        {
            buyin(coins);
            setSittingOut(false);
        }
    }

    /**
     * sit down or stand up temporarily
     * @param sittingOut
     */
    void setSittingOut(boolean sittingOut);

    /**
     * Player uses that seat
     * @param player
     */
    void join(PlayerBO player);
    
    /**
     * Player stands up and makes the seat free.
     * @return the amount of coins on seat (in play are lost)
     */
    int leave();

    /**
     * Player transfers coins from balance to seat
     * @param coins
     */
    void buyin(int coins);

    int getAmountOfCoinsOnSeat();
}
