package de.hatoka.poker.table.capi.business;

import java.util.Optional;

import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;

/**
 * A table can attend at multiple tournaments as competitor. A table is a real
 * person and can be an user, but doesn't need to be. Tables can be registered in context of
 * an user (known table), a tournament (competitor) or cash game (visitor)
 */
public interface TableBO
{
    /**
     * @return unique identifier of table in specific context.
     */
	TableRef getRef();

	/**
     * @return name of table
     */
    String getName();

	/**
     * @return variant of poker of table
     */
    PokerVariant getVariant();

    /**
     * @return limit of table
     */
    PokerLimit getLimit();

    /**
     * Removes table
     */
    void remove();

    /**
     * @return internal identifier for foreign keys
     */
    Long getInternalId();
    
    /**
     * @return seat repo to handle seats of table
     */
    SeatBORepository getSeatRepository();
    
    /**
     * dealer starts new game at table (dealer is responsible for correct start and preconditions)
     * @return game
     */
    GameBO newGame();

    /**
     * @return current game if there is a running game
     */
    Optional<GameBO> getCurrentGame();

    /**
     * Dealer needs information from last game (like dealer, blinds)
     * @return last game if there is one
     */
    Optional<DealerGameCommunication> getLastGame();

    /**
     * @return predefined small blind for table
     */
    Integer getSmallBlind();

    /**
     * @return predefined big blind for table
     */
    Integer getBigBlind();

    /**
     * @return maximal number of coins on table via rebuy
     */
    Integer getMaxBuyIn();
}
