package de.hatoka.poker.table.internal.remote;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import de.hatoka.poker.remote.TableInfoRO;
import de.hatoka.poker.remote.TableRO;
import de.hatoka.poker.table.capi.business.TableBO;

@Component
public class TableBO2RO
{
    public TableRO apply(TableBO table)
    {
        TableInfoRO info = new TableInfoRO();
        info.setName(table.getName());
        info.setVariant(table.getVariant().name());
        info.setLimit(table.getLimit().name());
        info.setMaxBuyIn(table.getMaxBuyIn());

        TableRO result = new TableRO();
        result.setRefGlobal(table.getRef().getGlobalRef());
        result.setRefLocal(table.getRef().getLocalRef());
        result.setResourceURI("/tables/" + table.getRef().getLocalRef());
        result.setInfo(info);
        return result;
    }

    public List<TableRO> apply(Collection<TableBO> projects)
    {
        return projects.stream().map(this::apply).collect(Collectors.toList());
    }
}
