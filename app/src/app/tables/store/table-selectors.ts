import { createSelector, createFeatureSelector } from '@ngrx/store';

import { Table } from './table-models';
import { tableFeatureKey, TableState } from './table-reducers';

const selectFeature = createFeatureSelector<TableState>(tableFeatureKey);

export const selectAllTables = createSelector(selectFeature, getAllTablesFromState);
function getAllTablesFromState (state: TableState) : ReadonlyMap<string, Table>
{
  return state.tables;
}
