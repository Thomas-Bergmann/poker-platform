package de.hatoka.poker.table.internal.event;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TableInfo
{
    @Autowired
    private TableBORepository tableRepository;

    private final TableRef tableRef;

    public TableInfo(TableRef tableRef)
    {
        this.tableRef = tableRef;
    }

    public Optional<GameInfo> getCurrentGame()
    {
        return getTable().getCurrentGame().map(GameInfo::new);
    }

    public Optional<GameInfo> getLastGame()
    {
        return getTable().getLastGame().map(GameInfo::new);
    }

    public List<SeatBO> getReadySeats()
    {
        return getTable().getSeatRepository().getReadySeats();
    }

    public List<SeatRef> getAllSeats()
    {
        return getTable().getSeatRepository().getSeats().stream().map(SeatBO::getRef).toList();
    }

    public Integer getNextBigBlind()
    {
        return getTable().getBigBlind();
    }

    public Integer getNextSmallBlind()
    {
        return getTable().getSmallBlind();
    }

    public GameInfo newGame()
    {
        return new GameInfo(getTable().newGame());
    }

    private TableBO getTable()
    {
        return tableRepository.getTable(tableRef);
    }
}
