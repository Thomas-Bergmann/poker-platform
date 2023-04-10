package de.hatoka.poker.table.internal.business;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.GameBO;
import de.hatoka.poker.table.capi.business.GameRef;
import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.event.history.DealerEvent;
import de.hatoka.poker.table.capi.event.history.GameEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.ErrorEvent;
import de.hatoka.poker.table.capi.event.history.lifecycle.TransferEvent;
import de.hatoka.poker.table.capi.event.history.seat.PlayerEvent;
import de.hatoka.poker.table.internal.json.GameEventType;
import de.hatoka.poker.table.internal.persistence.GameEventDao;
import de.hatoka.poker.table.internal.persistence.GameEventPO;
import jakarta.transaction.Transactional;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GameBOImpl implements GameBO
{
    @Autowired
    private GameEventDao gameEventDao;

    private final TableBO table;
    private final Long gameNo;

    public GameBOImpl(TableBO table, Long gameNo)
    {
        this.table = table;
        this.gameNo = gameNo;
    }

    @Override
    @Transactional
    public void publishEvent(DealerEvent event)
    {
        switch(GameEventType.valueOf(event.getClass()))
        {
            case TransferEvent:
                transfer((TransferEvent)event);
                break;
            default:
                break;
        }
        saveEvent(event);
    }

    @Override
    public void publishEvent(PlayerEvent event)
    {
        saveEvent(event);
    }

    @Transactional
    private void transfer(TransferEvent event)
    {
        event.getCoinsOnSeatDiffOfGame().forEach((seat, coins) -> updateCoins(seat, coins));
    }

    private void updateCoins(SeatRef seat, Integer coins)
    {
        try
        {
            getSeat(seat).updateCoinsOnSeat(coins);
        }
        catch(Exception e)
        {
            saveEvent(ErrorEvent.valueOf(e));
        }
    }

    private void saveEvent(GameEvent event)
    {
        // sure that events are loaded
        int nextEventNo = getAllEvents().size();
        GameEventType type = GameEventType.valueOf(event.getClass());
        GameEventPO eventPO = new GameEventPO();
        eventPO.setTableId(table.getInternalId());
        eventPO.setGameNo(gameNo);
        eventPO.setEventNo(nextEventNo);
        eventPO.setEventType(type.name());
        eventPO.setEventData(type.serialize(event));
        LoggerFactory.getLogger(getClass())
                     .info("{}@{} event-stored: {}:{}", gameNo, table.getRef().getLocalRef(), type.name(),
                                     type.serialize(event));
        gameEventDao.save(eventPO);
    }

    private GameEvent mapToEvent(GameEventPO po)
    {
        return GameEventType.valueOf(po.getEventType()).deserialize(po.getEventData());
    }

    public SeatBO getSeat(SeatRef seatRef)
    {
        return table.getSeatRepository().findSeat(seatRef).get();
    }

    @Override
    public TableBO getTable()
    {
        return table;
    }

    @Override
    public GameRef getRef()
    {
        return GameRef.localRef(table.getRef(), gameNo);
    }

    @Override
    public List<GameEvent> getAllEvents()
    {
        return gameEventDao.getByTableidAndGameno(table.getInternalId(), gameNo)
                           .stream()
                           .map(this::mapToEvent)
                           .collect(Collectors.toList());
    }
}
