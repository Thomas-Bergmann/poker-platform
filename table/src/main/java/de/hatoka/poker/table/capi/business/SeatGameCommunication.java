package de.hatoka.poker.table.capi.business;

import java.util.Optional;

import de.hatoka.poker.player.capi.business.PlayerBO;

/**
 * The Dealer (or indirect the Game) applies changes to the seat or requires information from seat.
 */
public interface SeatGameCommunication
{
    /**
     * @return player at seat if there is one
     */
    Optional<PlayerBO> getPlayer();

    /**
     * @return true if there is no player
     */
    default boolean isFree()
    {
        return getPlayer().isEmpty();
    }

    /**
     * @return true if player is out (short time)
     */
    boolean isSittingOut();

    /**
     * @return true if player is ready for play
     */
    default boolean isInPlay()
    {
        return !isFree() && !isSittingOut();
    }

    /**
     * After a game the dealer defines the moving coins from/to seat. positive coins means additional coins on seat (player wins).
     */
    void updateCoinsOnSeat(int coins);
}
