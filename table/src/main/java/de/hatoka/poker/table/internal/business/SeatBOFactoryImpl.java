package de.hatoka.poker.table.internal.business;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.SeatRef;

@Component
public class SeatBOFactoryImpl implements SeatBOFactory
{
    @Lookup
    @Override
    public SeatBO get(SeatRef seatRef, SeatBORepository repository)
    {
        // done by @Lookup
        return null;
    }
}
