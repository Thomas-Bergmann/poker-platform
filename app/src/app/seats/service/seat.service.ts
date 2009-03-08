import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Seat } from 'src/app/seats/store';
import { ApiService } from 'src/app/core/service';
import { Table } from '../../tables';

export class SeatDataRO {
  'player-ref' : string = "";
  'coins-onseat' : number;
  'sitting-out' : boolean;
  'cards-hole' : string = "";

  init(seat : Seat)
  {
    this['player-ref'] = seat.playerRef;
    this['sitting-out'] = seat.isOutSeat;
    this['coins-onseat'] = seat.coinsOnSeat;
    return this;
  }
}
export interface SeatInfoRO {
  position : number;
  tableResourceURI: string;
  name: string;
  rank: string;
  'cards-hole' : string;
  'coins-inplay': number;
  'allin' : boolean;
  'button' : boolean;
  'has-action' : boolean;
  'out' : boolean;
}

export interface SeatRO {
  refLocal : string;
  refGlobal : string;
  resourceURI: string;
  data : SeatDataRO;
  info : SeatInfoRO;
}

@Injectable({ providedIn: 'root' })
export class SeatService {
  constructor(private apiService: ApiService) {}

  getSeats(table : Table): Observable<Seat[]> {
    return this.apiService
      .get<SeatRO[]>(`${table.resourceURI}/seats`)
      .pipe(map(ros => this.convertListSeatRO(ros)));
  }

  addSeat(seat : Seat): Observable<{}> {
    var data : SeatDataRO = new SeatDataRO().init(seat);
    return this.apiService.post(`${seat.tableResourceURI}/seats/joinTable`, data);
  }

  updateSeat(seat : Seat): Observable<{}> {
    var data : SeatDataRO = new SeatDataRO().init(seat);
    return this.apiService.patch(seat.resourceURI, data);
  }

  deleteSeat(seat: Seat): Observable<{}> {
    return this.apiService.delete(seat.resourceURI);
  }

  convertListSeatRO(ros : SeatRO[]): Seat[]
  {
    return ros.map(ro => convertSeatRO(ro));
  }
}

function convertSeatRO(ro : SeatRO): Seat
{
  return new Seat().init(ro.resourceURI, ro.info.tableResourceURI, ro.info.position, 
    ro.data['player-ref'], ro.info.out, ro.data['sitting-out'], ro.data['coins-onseat'], ro.info['coins-inplay'],
    ro.info.name,
    ro.info['cards-hole'], ro.info.allin, ro.info.button, ro.info['has-action'], ro.info.rank);
}
