package de.hatoka.poker.table.capi.event.game;

import java.util.Optional;

import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.event.history.lifecycle.TransferEvent;

/**
 * Describes the action of a dealer inside of the game does not change the table or seat state As result of the game a
 * {@link TransferEvent} can be created to apply the result to seats->players
 */
public interface DealerInGame
{
    /**
     * Dealer starts an initialized
     */
    default void start()
    {
        blindsAndAnte();
        holeCards();
    }

    /**
     * Dealer requests blinds and ante
     */
    void blindsAndAnte();

    /**
     * Dealer gives hole cards to all players
     */
    void holeCards();

    /**
     * Dealer burns a card
     */
    void burnCard();

    /**
     * Dealer places cards on board (open for all)
     * @param amount of cards
     */
    void giveBoardCards(int amount);

    default void flop()
    {
        collectCoins();
        if (isOneLeft())
        {
            payout();
        }
        else
        {
            burnCard();
            giveBoardCards(3);
        }
    }

    default void turn()
    {
        collectCoins();
        if (isOneLeft())
        {
            payout();
        }
        else
        {
            burnCard();
            giveBoardCards(1);
        }
    }

    default void river()
    {
        collectCoins();
        if (isOneLeft())
        {
            payout();
        }
        else
        {
            burnCard();
            giveBoardCards(1);
        }
    }

    boolean isOneLeft();

    /**
     * Collect coins from seats (players) to pots on table
     */
    void collectCoins();

    /**
     * Calculates the winner of the game
     */
    void payout();

    /**
     * defines bet hands and calculates winner
     */
    default void showDown()
    {
        collectCoins();
        payout();
    }

    /**
     * abort a game if it sucks
     */
    void abort();

    /**
     * @return true if dealer may need to do additional steps (so no player is active)
     */
    boolean doWhatEverYouNeed();

    /**
     * @return true if dealer has showdown event and can transfer coins
     */
    boolean canTransfer();

    /**
     * @return created transfer document, but did not apply it. dealer in game doesn't communicate with table or seat
     *         directly.
     */
    TransferEvent getTransfer();

    /**
     * @return seat who has action currently, to check that the player is still on table.
     */
    Optional<SeatRef> getSeatWithAction();

    /**
     * Dealer aborts game for player (e.g. stand-up or timeout)
     * @param seatRef seat is disqualified
     */
    void abort(SeatRef seatRef);
}
