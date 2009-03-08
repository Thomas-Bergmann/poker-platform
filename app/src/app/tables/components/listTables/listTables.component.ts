import { Component, EventEmitter, Input, Output} from '@angular/core';

import { Table } from 'src/app/tables/store';

@Component({
  selector: 'listTables',
  templateUrl: './listTables.component.html',
  styleUrls: ['./listTables.component.sass']
})
export class ListTablesComponent {
  @Input() tables!: readonly Table[];
  @Input() selectedTable? : Table;
  @Output() selectTable = new EventEmitter<Table>();

  public select(p: Table): void {
    this.selectTable.emit(p);
  }
}
