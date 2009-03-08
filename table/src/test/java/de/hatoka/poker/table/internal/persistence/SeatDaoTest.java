package de.hatoka.poker.table.internal.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.poker.table.capi.business.TableRef;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class SeatDaoTest
{
    private static final TableRef TABLE_REF_ONE = TableRef.localRef("table-one");

    @Autowired
    private SeatDao seatDao;
    @Autowired
    private TableDao tableDao;

    private TablePO tablePO;

    @BeforeEach 
    public void createTable()
    {
        tablePO = tableDao.save(newTablePO(TABLE_REF_ONE.getName()));
    }

    @AfterEach
    public void deleteRepo()
    {
        tableDao.delete(tablePO);
    }

    private TablePO newTablePO(String name)
    {
        TablePO result = new TablePO();
        result.setName(name);
        result.setLimit(PokerLimit.NO_LIMIT.name());
        result.setVariant(PokerVariant.TEXAS_HOLDEM.name());
        return result;
    }

    private SeatPO newSeatPO(Integer position)
    {
        SeatPO result = new SeatPO();
        result.setTableId(tablePO.getId());
        result.setPosition(position);
        return result;
    }

    @Test
    public void testCRUD()
    {
        SeatPO seatPO = newSeatPO(1);
        seatDao.save(seatPO);
        Optional<SeatPO> findSeatPO= seatDao.findByTableidAndPosition(tablePO.getId(), 1);
        assertEquals(seatPO, findSeatPO.get());
        seatDao.delete(seatPO);
    }

}
