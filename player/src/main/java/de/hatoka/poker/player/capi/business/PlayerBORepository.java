package de.hatoka.poker.player.capi.business;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.hatoka.user.capi.business.UserRef;

public interface PlayerBORepository
{
    /**
     * Creates a player in context of a user.
     * @param playerRef player reference for user
     * @return created player
     */
    PlayerBO createHumanPlayer(UserRef userRef);
    
    /**
     * Updates a player with user information
     * @param playerRef
     * @param userRef
     */
    void updateHumanPlayer(UserRef userRef);

    /**
     * Creates a bot (computed) player in context of a user.
     * @param userRef owner of bot
     * @param botName
     * @return created player
     */
    PlayerBO createBotPlayer(UserRef userRef, String botName);

    /**
     * Find a player
     * @param playerRef player
     * @return player found or not
     */
    default Optional<PlayerBO> findPlayer(PlayerRef playerRef)
    {
        return playerRef.isHuman() ? findHuman(playerRef.getUserRef()) : findBot(playerRef.getUserRef(), playerRef.getName());
    }

    /**
     * @param userRef user 
     * @return player assigned to user as human
     */
    Optional<PlayerBO> findHuman(UserRef userRef);

    /**
     * @param userRef user 
     * @return list of bots owned by user
     */
    List<PlayerBO> getBots(UserRef userRef);

    /**
     * @param userRef
     * @param name
     * @return the bot of user with given name
     */
    default Optional<PlayerBO> findBot(UserRef userRef, String name)
    {
        return getBots(userRef).stream().filter(b -> name.equals(b.getRef().getName())).findAny();
    }

    /**
     * resolved a player by given internal id, 
     *   especially if the caller knows that the player exists (like by foreign keys)
     * @param internalPlayerId internal player id for persistence
     * @return player
     */
    default PlayerBO getPlayer(Long internalPlayerId)
    {
        return findPlayer(internalPlayerId).get();
    }

    /**
     * Find a player
     * @param internalPlayerId internal player id for persistence
     * @return player found or not
     */
    Optional<PlayerBO> findPlayer(Long internalPlayerId);

    /**
     * Removes all users of this repository
     */
    default void clear()
    {
        getAllPlayers().forEach(PlayerBO::remove);
    }

    /**
     * @return all registered players
     */
    Collection<PlayerBO> getAllPlayers();

    default Collection<PlayerBO> getAllHumanPlayers()
    {
        return getAllPlayers().stream().filter(p -> p.getRef().isHuman()).toList();
    }
}
