package de.hatoka.poker.table.capi.event.game;

import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.internal.event.GameInfo;
import de.hatoka.poker.table.internal.event.PlayerActions;
import de.hatoka.poker.table.internal.event.PlayerGameInfo;

public interface PlayerFactory
{
    PlayerActions get(GameInfo gameInfo, SeatRef seat);
    PlayerActions get(SeatBO seat);
    
    PlayerGameInfo getInfo(GameInfo gameInfo, SeatRef seat);
    PlayerGameInfo getInfo(SeatBO seat);
}
