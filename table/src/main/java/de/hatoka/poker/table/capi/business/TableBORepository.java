package de.hatoka.poker.table.capi.business;

import java.util.Collection;
import java.util.Optional;

import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;

public interface TableBORepository
{
    /**
     * Creates a table in context of a user.
     * @param tableRef table reference for user
     * @return created table
     */
    TableBO createTable(TableRef tableRef, PokerVariant variant, PokerLimit limit);

    /**
     * @param tableRef table
     * @return table that must exist
     */
    TableBO getTable(TableRef tableRef);

    /**
     * Find a table
     * @param tableRef table
     * @return table found or not
     */
    Optional<TableBO> findTable(TableRef tableRef);

    /**
     * resolved a table by given internal id, 
     *   especially if the caller knows that the table exists (like by foreign keys)
     * @param internalTableId internal table id for persistence
     * @return table
     */
    default TableBO getTable(Long internalTableId)
    {
        return findTable(internalTableId).get();
    }

    /**
     * Find a table
     * @param internalTableId internal table id for persistence
     * @return table found or not
     */
    Optional<TableBO> findTable(Long internalTableId);

    /**
     * Removes all users of this repository
     */
    default void clear()
    {
        getAllTables().forEach(TableBO::remove);
    }

    /**
     * @return all registered tables
     */
    Collection<TableBO> getAllTables();
}
