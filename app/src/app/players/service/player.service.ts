import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Player } from 'src/app/players/store';
import { ApiService } from 'src/app/core/service';

export class PlayerDataRO {
  'nick-name' : string = "";
  'owner-ref' : string = "";
}
interface PlayerInfoRO {
  balance : number;
}

interface PlayerRO {
  refLocal : string;
  refGlobal : string;
  data : PlayerDataRO;
  info : PlayerInfoRO;
}

@Injectable({ providedIn: 'root' })
export class PlayerService {
  constructor(private apiService: ApiService) {}

  getPlayers(): Observable<Player[]> {
    return this.apiService
      .get<PlayerRO[]>(`/players`)
      .pipe(map(this.convertListPlayerRO));
  }

  convertListPlayerRO(ros : PlayerRO[]): Player[]
  {
    return ros.map(convertPlayerRO);
  }
}

function convertPlayerRO(ro : PlayerRO): Player
{
  return new Player().init(ro.refGlobal, ro.refLocal, ro.data['owner-ref'], ro.data['nick-name']);
}
