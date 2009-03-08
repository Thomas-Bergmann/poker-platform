import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Table } from 'src/app/tables/store';
import { ApiService } from 'src/app/core/service';

interface TableInfoRO {
  name : string;
  variant: string;
  limit: string;
  'max-buyin': number;
}

interface TableRO {
  refLocal : string;
  refGlobal : string;
  resourceURI: string;
  info : TableInfoRO;
}

@Injectable({ providedIn: 'root' })
export class TableService {
  constructor(private apiService: ApiService) {}

  getTables(): Observable<Table[]> {
    return this.apiService
      .get<TableRO[]>(`/tables`)
      .pipe(map(ros => this.convertListTableRO(ros)));
  }

  deleteTable(table : Table): Observable<{}> {
    return this.apiService.delete(table.resourceURI);
  }

  convertListTableRO(ros : TableRO[]): Table[]
  {
    return ros.map(ro => convertTableRO(ro));
  }
}

function convertTableRO(ro : TableRO): Table
{
  return new Table().init(ro.resourceURI, ro.info.name, ro.info.variant, ro.info.limit, ro.info['max-buyin']);
}
