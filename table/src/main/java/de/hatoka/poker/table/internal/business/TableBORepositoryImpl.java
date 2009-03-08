package de.hatoka.poker.table.internal.business;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;
import de.hatoka.poker.table.internal.persistence.TableDao;
import de.hatoka.poker.table.internal.persistence.TablePO;

@Component
public class TableBORepositoryImpl implements TableBORepository
{
    @Autowired
    private TableDao tableDao;
    @Autowired
    private TableBOFactory tableBOFactory;

    @Override
    public TableBO createTable(TableRef tableRef, PokerVariant variant, PokerLimit limit)
    {
        TablePO po = new TablePO();
        po.setName(tableRef.getName());
        po.setVariant(variant.name());
        po.setLimit(limit.name());
        tableDao.save(po);
        return getTable(tableRef);
    }

    @Override
    public TableBO getTable(TableRef tableRef)
    {
        return tableBOFactory.get(tableRef);
    }

    @Override
    public Optional<TableBO> findTable(TableRef tableRef)
    {
        LoggerFactory.getLogger(getClass()).trace("get tablepo by name {}", tableRef.getGlobalRef());
        return tableDao.findByName(tableRef.getName()).map(tableBOFactory::get);
    }

    @Override
    public Collection<TableBO> getAllTables()
    {
        LoggerFactory.getLogger(getClass()).debug("get tablepo by all");
        return tableDao.findAll().stream().map(tableBOFactory::get).collect(Collectors.toList());
    }

    @Override
    public Optional<TableBO> findTable(Long internalTableId)
    {
        LoggerFactory.getLogger(getClass()).debug("get tablepo by id {}", internalTableId);
        return tableDao.findById(internalTableId).map(tableBOFactory::get);
    }
}
