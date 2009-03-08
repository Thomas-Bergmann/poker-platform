package de.hatoka.poker.table.internal.business;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.SeatBO;
import de.hatoka.poker.table.capi.business.SeatBORepository;
import de.hatoka.poker.table.capi.business.SeatRef;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.internal.persistence.SeatDao;
import de.hatoka.poker.table.internal.persistence.SeatPO;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SeatBORepositoryImpl implements SeatBORepository
{
    private static final Comparator<SeatBO> SORT_BY_POSITION = (a,b) -> a.getPosition().compareTo(b.getPosition());

    @Autowired
    private SeatDao seatDao;
    @Autowired
    private SeatBOFactory seatBOFactory;
    @Autowired
    private TableBOFactory tableBOFactory;

    private final Long internalTableID;
    private final TableRef tableRef;

    public SeatBORepositoryImpl(long internalTableID, TableRef tableRef)
    {
        this.internalTableID = internalTableID;
        this.tableRef = tableRef;
    }
    
    @Override
    public SeatBO createSeat()
    {
        SeatPO po = new SeatPO();
        po.setTableId(internalTableID);
        po.setPosition(getFreePosition());
        return getSeatBO(seatDao.save(po));
    }

    private SeatBO getSeatBO(SeatRef seatRef)
    {
        return seatBOFactory.get(seatRef, this);
    }

    private SeatBO getSeatBO(SeatPO seatPO)
    {
        return getSeatBO(SeatRef.localRef(tableRef, seatPO.getPosition()));
    }

    private Integer getFreePosition()
    {
        return getSeats().stream().map(SeatBO::getPosition).max((a,b) -> a.compareTo(b)).orElse(-1) + 1;
    }

    @Override
    public Optional<SeatBO> findSeat(Integer position)
    {
        return seatDao.findByTableidAndPosition(internalTableID, position).map(this::getSeatBO);
    }

    @Override
    public List<SeatBO> getSeats()
    {
        return seatDao.findByTableid(internalTableID).stream().map(this::getSeatBO).sorted(SORT_BY_POSITION).collect(Collectors.toList());
    }

    @Override
    public Optional<SeatBO> findSeat(Long internalSeatId)
    {
        return seatDao.findById(internalSeatId).map(this::getSeatBO);
    }

    @Override
    public TableRef getTableRef()
    {
        return tableRef;
    }

    @Override
    public TableBO getTable()
    {
        return tableBOFactory.get(tableRef);
    }
}
