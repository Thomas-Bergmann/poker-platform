/*
 * Copyright (C) Intershop Communications AG - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * The content is proprietary and confidential.
 * Intershop Communication AG, Intershop Tower, 07740 Jena, Germany, 2018-04-05
 */
package de.hatoka.poker.table.internal.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.hatoka.poker.remote.TableCreateRO;
import de.hatoka.poker.remote.TableRO;
import de.hatoka.poker.rules.PokerLimit;
import de.hatoka.poker.rules.PokerVariant;
import de.hatoka.poker.table.capi.business.TableBORepository;
import de.hatoka.poker.table.capi.business.TableRef;
import tests.de.hatoka.poker.table.TableTestApplication;
import tests.de.hatoka.poker.table.TableTestConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { TableTestApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { TableTestConfiguration.class })
@ActiveProfiles("test")
public class TableControllerTest
{
    private static final TableRef PROJECT_REF1 = TableRef.localRef("name-1");
    private static final TableRef PROJECT_REF2 = TableRef.localRef("name-2");

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private TableBORepository repository;

    @BeforeEach @AfterEach
    public void deleteRepo()
    {
        repository.clear();
    }

    @Test
    public void createTableAndDelete()
    {
        TableCreateRO data = new TableCreateRO();
        data.setVariant(PokerVariant.TEXAS_HOLDEM.name());
        data.setLimit(PokerLimit.NO_LIMIT.name());
        putTable(PROJECT_REF1, data);

        TableRO ro = getTable(PROJECT_REF1);
        assertNotNull(ro, "project created and found");
        assertNotNull(ro.getInfo(), "project contains info");
        assertEquals("name-1", ro.getInfo().getName());
        deleteTable(PROJECT_REF1);
    }

    @Test
    public void testGetTables()
    {
        TableCreateRO data = new TableCreateRO();
        data.setVariant(PokerVariant.TEXAS_HOLDEM.name());
        data.setLimit(PokerLimit.NO_LIMIT.name());
        putTable(PROJECT_REF1, data);
        putTable(PROJECT_REF2, data);

        List<TableRO> projects = getTables();
        assertEquals(2, projects.size());
        deleteTable(PROJECT_REF1);
        deleteTable(PROJECT_REF2);
    }

    private List<TableRO> getTables()
    {
        Map<String, String> urlParams = new HashMap<>();
        return Arrays.asList(this.restTemplate.getForObject(TableController.PATH_ROOT, TableRO[].class, urlParams));
    }

    private Map<String, String> createURIParameter(TableRef ref)
    {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put(TableController.PATH_VAR_TABLEID, ref.getName());
        return urlParams;
    }

    private TableRO getTable(TableRef ref)
    {
        return this.restTemplate.getForObject(TableController.PATH_TABLE, TableRO.class, createURIParameter(ref));
    }

    private void putTable(TableRef ref, TableCreateRO data)
    {
        HttpEntity<TableCreateRO> entity = new HttpEntity<>(data);
        ResponseEntity<Void> response = this.restTemplate.exchange(TableController.PATH_TABLE, HttpMethod.PUT, entity , Void.class, createURIParameter(ref));
        assertTrue(response.getStatusCode().is2xxSuccessful(), "returned with " + response.getStatusCode());
        // this.restTemplate.put(TableController.PATH_TABLE, data, createURIParameter(ref));
    }

    private void deleteTable(TableRef ref)
    {
        this.restTemplate.delete(TableController.PATH_TABLE, createURIParameter(ref));
    }
}
