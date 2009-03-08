package de.hatoka.poker.table.internal.business;

import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.internal.persistence.TablePO;

public interface TableBOFactory
{
    TableBO get(TableRef ref);
    
    default TableBO get(TablePO po)
    {
        return get(TableRef.localRef(po.getName()));
    }
}
