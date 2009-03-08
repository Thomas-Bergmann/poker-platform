package de.hatoka.poker.table.internal.business;

import de.hatoka.poker.table.capi.business.GameBO;
import de.hatoka.poker.table.capi.business.TableBO;

public interface GameBOFactory
{
    GameBO get(TableBO table, Long gameNo);
}
