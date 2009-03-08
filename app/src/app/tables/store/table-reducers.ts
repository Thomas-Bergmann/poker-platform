import { Action, createReducer, on } from '@ngrx/store';
import { Table } from './table-models';
import { addTables, addTable, removeTable, updateTable } from './table-actions';

export const tableFeatureKey = 'tableState';

// keys tenantid and tableid
var initialTables : ReadonlyMap<string, Table> = new Map();
export interface TableState {
  tables: ReadonlyMap<string, Table>;
}

const initialState : TableState = {
  tables : initialTables,
}

const _tableReducer = createReducer(
  initialState,
  on(addTables, (state, action) => _updateTablesAtState(state, _setTables(state.tables, action.tables))),
  on(addTable, (state, action) => _updateTablesAtState(state, _setTable(state.tables, action.table))),
  on(removeTable, (state, action) => _updateTablesAtState(state, _removeTable(state.tables, action.table))),
  on(updateTable, (state, action) => _updateTablesAtState(state, _setTable(state.tables, action.table))),
);

export function tableReducer(state: TableState | undefined, action: Action) {
  return _tableReducer(state, action);
}

function _updateTablesAtState(state:TableState, newTables: ReadonlyMap<string, Table>):TableState
{
  return ({
    ...state,
    tables: newTables,
  });
}

function _setTable(tables:ReadonlyMap<string, Table>,  table:Table):ReadonlyMap<string, Table>
{
  let newTables : Map<string, Table> = new Map();
  tables?.forEach((v,k) => newTables.set(k,v));
  newTables.set(table.resourceURI, table);
  return newTables;
}

function _setTables(tables:ReadonlyMap<string, Table>,  setTables:Table[]):ReadonlyMap<string, Table>
{
  let newTables : ReadonlyMap<string, Table> = tables;
  setTables.forEach(table => {
    newTables = _setTable(newTables, table);
  });
  return newTables;
}

function _removeTable(tables : ReadonlyMap<string, Table>, table:Table):ReadonlyMap<string, Table>
{
  let newTables : Map<string, Table> = new Map();
  tables?.forEach((v,k) => newTables.set(k,v));
  newTables.delete(table.resourceURI);
  return newTables;
}
