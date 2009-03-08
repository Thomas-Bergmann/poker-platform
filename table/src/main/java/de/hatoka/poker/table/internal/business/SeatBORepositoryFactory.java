package de.hatoka.poker.table.internal.business;

import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.TableRef;

public interface SeatBORepositoryFactory
{
    SeatBORepository get(long tableId, TableRef tableRef);
}
