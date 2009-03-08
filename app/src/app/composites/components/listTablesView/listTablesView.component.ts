import { Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, Unsubscribable } from 'rxjs';

import { TableState, Table, selectAllTables } from 'src/app/tables/store';
import { TableFacade } from 'src/app/tables/service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'listTablesView',
  templateUrl: './listTablesView.component.html',
  styleUrls: ['./listTablesView.component.sass']
})

export class ListTablesViewComponent implements OnInit  {
  allTables$: Observable<ReadonlyMap<string, Table>>;
  tables: readonly Table[] = [];
  unsubscribeOnDestroy : Unsubscribable[] = [];

  constructor(
    private readonly tableStore: Store<TableState>,
    private readonly tableFacade: TableFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    this.allTables$ = this.tableStore.select(selectAllTables);
  }
  ngOnInit(): void {
    this.tableFacade.loadTables();
    this.allTables$.subscribe(allTables => {
      this.tables = Array.from(allTables?.values());
    }); 
  }
  ngOnDestroy() {
    this.unsubscribeOnDestroy.forEach(s => s.unsubscribe());
  }

  public onSelectTable(p: Table): void {
    this.router.navigate([p.name], { relativeTo: this.route.parent });
  }
}
