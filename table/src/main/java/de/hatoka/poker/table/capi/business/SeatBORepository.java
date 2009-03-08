package de.hatoka.poker.table.capi.business;

import java.util.List;
import java.util.Optional;

import de.hatoka.poker.player.capi.business.PlayerBO;
import de.hatoka.poker.player.capi.business.PlayerRef;

/**
 * Seat aspect for table
 */
public interface SeatBORepository
{
    /**
     * Creates a seat in context of a user.
     * 
     * @param seatRef seat reference for user
     * @return created seat
     */
    SeatBO createSeat();

    /**
     * Find a seat
     * 
     * @param seatRef seat
     * @return seat found or not
     */
    default Optional<SeatBO> findSeat(SeatRef seatRef)
    {
        return findSeat(seatRef.getPosition());
    }

    /**
     * Find a seat via position
     * 
     * @param position
     * @return seat found or not
     */
    Optional<SeatBO> findSeat(Integer position);

    /**
     * resolved a seat by given internal id, especially if the caller knows that the seat exists (like by foreign keys)
     * 
     * @param internalSeatId internal seat id for persistence
     * @return seat
     */
    default SeatBO getSeat(Long internalSeatId)
    {
        return findSeat(internalSeatId).get();
    }

    /**
     * Find a seat
     * 
     * @param internalSeatId internal seat id for persistence
     * @return seat found or not
     */
    Optional<SeatBO> findSeat(Long internalSeatId);

    /**
     * Removes all users of this repository
     */
    default void clear()
    {
        getSeats().forEach(SeatBO::remove);
    }

    /**
     * @return all registered seats, sorted by position of seat
     */
    List<SeatBO> getSeats();

    /**
     * Removes seats without players
     */
    default void clearEmptySeats()
    {
        getSeats().stream().filter(s -> s.getPlayer().isEmpty()).forEach(SeatBO::remove);
    }

    default List<SeatBO> getReadySeats()
    {
        return getSeats().stream().filter(SeatBO::isInPlay).toList();
    }

    default boolean isOnTable(PlayerBO player)
    {
        PlayerRef playerRef = player.getRef();
        return getSeats().stream()
                         .map(SeatBO::getPlayer)
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .map(PlayerBO::getRef)
                         .filter(r -> playerRef.equals(r))
                         .findAny()
                         .isPresent();
    }

    TableBO getTable();

    TableRef getTableRef();
}
