package de.hatoka.poker.table.internal.event;

import de.hatoka.poker.table.capi.business.TableRef;

public interface TableInfoFactory
{
    TableInfo get(TableRef tableRef);
}
