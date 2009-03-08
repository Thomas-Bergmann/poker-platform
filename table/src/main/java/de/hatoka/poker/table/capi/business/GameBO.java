package de.hatoka.poker.table.capi.business;

public interface GameBO extends DealerGameCommunication
{
    /**
     * @return table of the game
     */
    TableBO getTable();
    
    /**
     * @return reference
     */
    GameRef getRef();
}
