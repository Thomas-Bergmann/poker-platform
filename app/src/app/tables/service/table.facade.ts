import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { TableService } from './table.service';
import { TableState, Table, addTables, addTable, updateTable, removeTable } from 'src/app/tables/store';
import { ServiceEndpoint, ServiceState, updateServiceLocation } from 'src/app/core/service';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class TableFacade {
  areTablesLoaded : Map<string, boolean> = new Map();
  constructor(
    private readonly store: Store<TableState>,
    private readonly service : TableService,
    private readonly serviceStore: Store<ServiceState>,
  ) {
  }

  loadTables() {
    if (this.areTablesLoaded.get("root"))
    {
      return;
    }
    // console.log("loadTables table.serviceEndPoint", environment.serviceEndPoint);
    this.serviceStore.dispatch(updateServiceLocation(
      { serviceEndpoint : new ServiceEndpoint().init(environment.serviceEndPoint)}));
    this.areTablesLoaded.set("root", true);
    this.service.getTables()
      .subscribe(tables => {
        this.store.dispatch(addTables({ tables : tables}));
        this.areTablesLoaded.delete("root");
      });
  }
  deleteTable(table: Table) {
    this.store.dispatch(removeTable({ table : table}));
    this.service.deleteTable(table).subscribe();
  }
}
