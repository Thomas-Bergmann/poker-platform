package de.hatoka.poker.table.internal.business;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.TableRef;

@Component
public class SeatBORepositoryFactoryImpl implements SeatBORepositoryFactory
{
    @Lookup
    @Override
    public SeatBORepository get(long tableId, TableRef tableRef)
    {
        // done by @Lookup
        return null;
    }
}
