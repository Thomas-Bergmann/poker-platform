package de.hatoka.poker.table.capi.event.game;

import de.hatoka.poker.table.capi.business.TableBO;

public interface DealerFactory
{
    Dealer get(TableBO table);
}
