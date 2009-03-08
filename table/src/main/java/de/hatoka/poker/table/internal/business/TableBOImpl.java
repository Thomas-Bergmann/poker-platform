package de.hatoka.poker.table.internal.business;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.poker.table.capi.business.DealerGameCommunication;
import de.hatoka.poker.table.capi.business.GameBO;
import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.internal.persistence.GameEventDao;
import de.hatoka.poker.table.internal.persistence.TableDao;
import de.hatoka.poker.table.internal.persistence.TablePO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TableBOImpl implements TableBO
{
    @Autowired
    private TableDao tableDao;
    @Autowired
    private GameEventDao eventDao;
    @Autowired
    private SeatBORepositoryFactory seatRepoFactory;
    @Autowired
    private GameBOFactory gameFactory;

    static private TablePO cachedTablePO = null;
    static private TableRef ownerTablePO = null;

    private final TableRef tableRef;

    public TableBOImpl(TableRef tableRef)
    {
        this.tableRef = tableRef;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(tableRef);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TableBOImpl other = (TableBOImpl)obj;
        return Objects.equals(tableRef, other.tableRef);
    }

    @Override
    public String getName()
    {
        return getPO().getName();
    }

    @Override
    public void remove()
    {
        TablePO po = getPO();
        for (long i = po.getLastGameNo(); i > -1; i--)
        {
            eventDao.deleteAll(eventDao.getByTableidAndGameno(getInternalId(), i));
        }
        tableDao.delete(po);
    }

    private void savePO(TablePO tablePO)
    {
        tableDao.save(tablePO);
    }

    @Override
    public TableRef getRef()
    {
        return tableRef;
    }

    @Override
    public PokerVariant getVariant()
    {
        return PokerVariant.valueOf(getPO().getVariant());
    }

    @Override
    public PokerLimit getLimit()
    {
        return PokerLimit.valueOf(getPO().getLimit());
    }

    @Override
    public SeatBORepository getSeatRepository()
    {
        return seatRepoFactory.get(getInternalId(), getRef());
    }

    @Override
    public GameBO newGame()
    {
        TablePO po = getPO();
        Long nextGameNo = po.getLastGameNo() + 1L;
        po.setLastGameNo(nextGameNo);
        savePO(po);
        return gameFactory.get(this, nextGameNo);
    }

    @Override
    public Integer getSmallBlind()
    {
        return getPO().getSmallBlind();
    }

    @Override
    public Integer getBigBlind()
    {
        return getPO().getBigBlind();
    }

    @Override
    public Optional<GameBO> getCurrentGame()
    {
        Long lastGameNo = getPO().getLastGameNo();
        return lastGameNo.equals(0L) ? Optional.empty() : Optional.of(gameFactory.get(this, lastGameNo));
    }

    @Override
    public Optional<DealerGameCommunication> getLastGame()
    {
        TablePO po = getPO();
        if (po.getLastGameNo() > 1L)
        {
            return Optional.empty();
        }
        return Optional.of(gameFactory.get(this, po.getLastGameNo()));
    }

    private synchronized TablePO getPO()
    {
        if (cachedTablePO != null && ownerTablePO == this.tableRef)
        {
            return cachedTablePO;
        }
        TablePO result = null;
        if (ownerTablePO != this.tableRef)
        {
            LoggerFactory.getLogger(getClass()).trace("get tablepo by name {}", tableRef.getGlobalRef());
            result = tableDao.findByName(tableRef.getName()).get();
        }
        cachedTablePO = result;
        ownerTablePO = this.tableRef;
        return result;
    }

    @Override
    public Long getInternalId()
    {
        return getPO().getId();
    }

    @Override
    public Integer getMaxBuyIn()
    {
        return getPO().getMaxBuyIn();
    }
}
