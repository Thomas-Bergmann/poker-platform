package de.hatoka.poker.table.capi.business;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
public class TableBORepositoryTest
{
    private static final TableRef TABLE_REF_ONE = TableRef.localRef("table-one");
    private static final TableRef TABLE_REF_TWO = TableRef.localRef("table-two");

    @Autowired
    private TableBORepository repository;

    @BeforeEach @AfterEach
    public void deleteRepo()
    {
        repository.clear();
    }

    @Test
    public void testCrud()
    {
        TableBO project1 = repository.createTable(TABLE_REF_ONE, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT);
        TableBO project2 = repository.createTable(TABLE_REF_TWO, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT);
        Collection<TableBO> projects = repository.getAllTables();
        assertEquals(2, projects.size());
        assertTrue(projects.contains(project1));
        assertTrue(projects.contains(project2));
        project1.remove();
        projects = repository.getAllTables();
        assertEquals(1, projects.size());
        project2.remove();
        projects = repository.getAllTables();
        assertTrue(projects.isEmpty());
    }

}
