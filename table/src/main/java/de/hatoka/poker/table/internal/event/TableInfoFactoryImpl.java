package de.hatoka.poker.table.internal.event;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import de.hatoka.poker.table.capi.business.TableRef;

@Component
public class TableInfoFactoryImpl implements TableInfoFactory
{
    @Lookup
    @Override
    public TableInfo get(TableRef tableRef)
    {
        return null;
    }
}
