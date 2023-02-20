package de.hatoka.poker.table;

import java.util.List;

import de.hatoka.poker.base.Card;
import de.hatoka.poker.remote.SeatRO;

public interface PlayerGameInfo
{
    /**
     * @return if player has action
     */
    boolean hasAction();

    /**
     * @return hole cards of player
     */
    List<Card> getHoleCards();

    /**
     * @return size of pot (without current bet round)
     */
    Integer getPotSize();

    /**
     * @return current state of seats
     */
    List<SeatRO> getSeats();
}
