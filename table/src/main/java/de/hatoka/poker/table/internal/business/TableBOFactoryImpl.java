package de.hatoka.poker.table.internal.business;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableRef;

@Component
public class TableBOFactoryImpl implements TableBOFactory
{
    @Lookup
    @Override
    public TableBO get(TableRef ref)
    {
        // done by @Lookup
        return null;
    }
}
