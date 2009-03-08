package de.hatoka.poker.table.capi.business;

/**
 * A seat can attend at multiple tournaments as competitor. A seat is a real
 * person and can be an user, but doesn't need to be. Seats can be registered in context of
 * an user (known seat), a tournament (competitor) or cash game (visitor)
 */
public interface SeatBO extends SeatPlayerCommunication, SeatGameCommunication
{
    /**
     * @return unique identifier of seat in specific context.
     */
	SeatRef getRef();

	/**
     * @return name of seat
     */
    Integer getPosition();

	/**
     * @return table of seat
     */
    TableBO getTable();
    
    /**
     * Removes seat - similar to leave
     */
    default void remove()
    {
        leave();
    }

    /**
     * @return internal identifier for foreign keys
     */
    Long getInternalId();
}
