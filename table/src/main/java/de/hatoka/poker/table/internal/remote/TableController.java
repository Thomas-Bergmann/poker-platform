package de.hatoka.poker.table.internal.remote;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.hatoka.common.capi.rest.RestControllerErrorSupport;
import de.hatoka.poker.remote.TableCreateRO;
import de.hatoka.poker.remote.TableRO;
import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.poker.table.capi.business.TableBO;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;

@RestController
@RequestMapping(value = TableController.PATH_ROOT, produces = { APPLICATION_JSON_VALUE })
public class TableController
{
    public static final String PATH_ROOT = "/tables";
    public static final String PATH_SUB_TABLE = "/{tableid}";
    public static final String PATH_VAR_TABLEID= "tableid";
    public static final String PATH_TABLE = PATH_ROOT + PATH_SUB_TABLE;
    
    @Autowired
    private TableBORepository tableRepository;
    @Autowired
    private TableBO2RO tableBO2RO;

    @Autowired
    private RestControllerErrorSupport errorSupport;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<TableRO> getTables()
    {
        Collection<TableBO> tables = tableRepository.getAllTables();
        return tableBO2RO.apply(tables);
    }

    @PutMapping(value = PATH_SUB_TABLE, consumes = { APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    public void createTable(@PathVariable(PATH_VAR_TABLEID) String tableID, @RequestBody TableCreateRO input)
    {
        TableRef tableRef = TableRef.localRef(tableID);
        Optional<TableBO> tableOpt = tableRepository.findTable(tableRef);
        if (tableOpt.isPresent())
        {
            errorSupport.throwNotFoundException("found.table", tableRef.toString());
        }
        tableRepository.createTable(tableRef, PokerVariant.valueOf(input.getVariant()), PokerLimit.valueOf(input.getLimit()));
    }

    @GetMapping(PATH_SUB_TABLE)
    @ResponseStatus(HttpStatus.OK)
    public TableRO getTable(@PathVariable(PATH_VAR_TABLEID) String tableID)
    {
        TableRef tableRef = TableRef.localRef(tableID);
        Optional<TableBO> tableOpt = tableRepository.findTable(tableRef);
        if (!tableOpt.isPresent())
        {
            errorSupport.throwNotFoundException("notfound.table", tableRef.toString());
        }
        return tableBO2RO.apply(tableOpt.get());
    }

    @DeleteMapping(PATH_SUB_TABLE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteTable(@PathVariable(PATH_VAR_TABLEID) String tableID)
    {
        TableRef tableRef = TableRef.localRef(tableID);
        Optional<TableBO> tableOpt = tableRepository.findTable(tableRef);
        if (tableOpt.isPresent())
        {
            tableOpt.get().remove();
        }
    }
}
