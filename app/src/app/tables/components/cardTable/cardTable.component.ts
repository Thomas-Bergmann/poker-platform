import { Component, Input } from '@angular/core';

import { Table } from 'src/app/tables/store/table-models';
@Component({
  selector: 'cardTable',
  templateUrl: './cardTable.component.html',
  styleUrls: ['./cardTable.component.sass']
})
export class CardTableComponent {
  @Input() table!: Table;
  @Input() selectedTable?: Table;
}
