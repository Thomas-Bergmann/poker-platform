import { createAction, props } from '@ngrx/store';
import { Table } from './table-models';

export const addTables = createAction(
  'Add Tables',
  props<{ tables: Table[] }>()
);

export const addTable = createAction(
  'Add Table',
  props<{ table: Table }>()
);

export const removeTable = createAction(
  'Remove Table',
  props<{ table: Table }>()
);

export const updateTable = createAction(
  'Update Table',
  props<{ table: Table }>()
)
