package de.hatoka.poker.table.internal.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.event.game.Dealer;
import de.hatoka.poker.table.capi.event.game.DealerFactory;

@Component
public class DealerFactoryImpl implements DealerFactory
{
    @Autowired
    private TableInfoFactory tableInfoFactory;

    @Override
    public Dealer get(TableBO table)
    {
        return new DealerImpl(tableInfoFactory.get(table.getRef()));
    }
}
