package de.hatoka.poker.table.internal.event;

import java.util.Collection;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import de.hatoka.oidc.capi.event.UserTokenGeneratedEvent;
import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;

@Component
public class TableEventListener implements ApplicationListener<UserTokenGeneratedEvent>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TableEventListener.class);
    @Autowired
    private TableBORepository tableRepo;
    
    @Override
    public void onApplicationEvent(UserTokenGeneratedEvent event)
    {
        Collection<TableBO> tables = tableRepo.getAllTables();
        if (tables.isEmpty())
        {
            TableRef tableRef = TableRef.localRef(UUID.randomUUID().toString());
            tableRepo.createTable(tableRef, PokerVariant.TEXAS_HOLDEM, PokerLimit.NO_LIMIT);
            LOGGER.info("table created {}.", tableRef);
        }
    }
}
