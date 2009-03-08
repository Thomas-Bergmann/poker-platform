package de.hatoka.poker.table.internal.business;

import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.SeatRef;

public interface SeatBOFactory
{
    SeatBO get(SeatRef seatRef, SeatBORepository repository);
}
