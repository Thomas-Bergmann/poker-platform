package de.hatoka.poker.table.capi.event.game;

import de.hatoka.poker.table.capi.event.history.lifecycle.TransferEvent;

public interface DealerOnTable
{
    /**
     * @return true if dealer can start new game (but doesn't start it)
     */
    boolean canInitialize();

    /**
     * Dealer shuffles and initializes seats, pot, deck
     */
    void init();

    /**
     * Dealer transfers win/lost money to seat
     */
    void transfer(TransferEvent transfer);
}
