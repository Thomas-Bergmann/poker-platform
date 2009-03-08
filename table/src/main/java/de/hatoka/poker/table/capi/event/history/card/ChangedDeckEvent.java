package de.hatoka.poker.table.capi.event.history.card;

/**
 * GameChangedDeckEvent marks an event that manipulates the deck 
 */
public interface ChangedDeckEvent
{
    int getNumberOfCards();
}
