package de.hatoka.poker.table.internal.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TableTestConfiguration.class })
public class TableDaoTest
{
    private static final String NAME = "TableDaoTest-N";

    @Autowired
    private TableDao projectDao;

    @Test
    public void testCRUD()
    {
        TablePO TablePO = newTablePO(NAME);
        projectDao.save(TablePO);
        Optional<TablePO> findTablePO= projectDao.findByName(NAME);
        assertEquals(TablePO, findTablePO.get());
        projectDao.delete(TablePO);
    }

    private TablePO newTablePO(String name)
    {
        TablePO result = new TablePO();
        result.setName(name);
        result.setLimit(PokerLimit.NO_LIMIT.name());
        result.setVariant(PokerVariant.TEXAS_HOLDEM.name());
        return result;
    }
}
