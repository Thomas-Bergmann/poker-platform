package de.hatoka.poker.table.internal.business;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.GameBO;
import de.hatoka.poker.table.capi.business.TableBO;

@Component
public class GameBOFactoryImpl implements GameBOFactory
{
    @Lookup
    @Override
    public GameBO get(TableBO table, Long gameNo)
    {
        // done by @Lookup
        return null;
    }
}
