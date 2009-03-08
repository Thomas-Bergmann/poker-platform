package de.hatoka.poker.table.capi.event.game;

import java.util.Optional;

public interface Dealer
{
    /**
     * @return dealer with interacts before game is started
     */
    DealerOnTable getDealerOnTable();

    /**
     * @return dealer with interacts in the game (uses only game events)
     */
    Optional<DealerInGame> getDealerInGame();

    /**
     * Dealer does what is necessary
     */
    void doWhatEverYouNeed();
}
