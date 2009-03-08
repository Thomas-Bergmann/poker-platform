package de.hatoka.poker.player.capi.business;

import de.hatoka.user.capi.business.UserRef;

/**
 * A player can attend at multiple tournaments as competitor. A player is a real
 * person and can be an user, but doesn't need to be. Players can be registered in context of
 * an user (known player), a tournament (competitor) or cash game (visitor)
 */
public interface PlayerBO
{
    /**
     * @return unique identifier of player in specific context.
     */
	PlayerRef getRef();

	/**
     * @return name of player
     */
    String getNickName();

    /**
     * set nick name
     * @param nickName
     */
    void setNickName(String nickName);

    /**
     * @return balance of player (won/lost at tables)
     */
    int getBalance();

    /**
     * @return human or computer move
     */
    PlayerType getType();

    /**
     * Removes player
     */
    void remove();

    /**
     * @return internal identifier for foreign keys
     */
    Long getInternalId();

    /**
     * @return owner of player
     */
    UserRef getOwnerRef();

    /**
     * transfers money from player to seat (means reduces the balance of player)
     * @param seatRef
     * @param coins
     */
    void moveCoinsFromPlayerToSeat(String seatRef, int coins);

    /**
     * transfers money from seat to player (means increases the balance of player)
     * @param globalRef
     * @param coins
     */
    void moveCoinsFromSeatToPlayer(String globalRef, int coins);
}
