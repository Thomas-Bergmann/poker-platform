import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Game } from 'src/app/game/store';
import { ApiService } from 'src/app/core/service';
import { Seat, SeatFacade, SeatRO } from 'src/app/seats';

interface GameInfoRO {
  tableResourceURI: string;
  gameno: number,
  seats: SeatRO[],
  'cards-board': string,
  potsize: number,
  'blind-big' : number,
}

interface GameRO {
  refLocal : string;
  refGlobal : string;
  resourceURI: string;
  info : GameInfoRO;
}

@Injectable({ providedIn: 'root' })
export class GameService {
  constructor(
    private readonly apiService: ApiService,
    private readonly seatFacade: SeatFacade,
    ) {}

  getCurrent(seat : Seat): Observable<Game> {
    return this.apiService
      .get<GameRO>(`${seat.tableResourceURI}/games/current;seat=${seat.position};`)
      .pipe(map(ro => this.convertGameRO(ro)));
  }


  convertGameRO(ro : GameRO): Game
  {
    this.seatFacade.updatedSeats(ro.info.seats.map(s => this.map(s)));
    return new Game().init(ro.resourceURI, ro.info.tableResourceURI, ro.refLocal, ro.info['cards-board'], ro.info.potsize, ro.info['blind-big']);
  }

  map(ro : SeatRO): Seat
  {
    return new Seat().init(ro.resourceURI, ro.info.tableResourceURI, ro.info.position, ro.data['player-ref'],
      ro.data['sitting-out'], ro.info.out,
      ro.data['coins-onseat'], ro.info['coins-inplay'], ro.info.name,
      ro.info['cards-hole'], ro.info.allin, ro.info.button, ro.info['has-action'], ro.info.rank);
  }
  doAction(seat: Seat, action: string, betTo:number) : Observable<Game>
  {
    return this.apiService
      .post<GameRO>(`${seat.tableResourceURI}/games/current;seat=${seat.position};/action`, {
        action : action,
        'bet-to' : betTo
       })
       .pipe(map(ro => this.convertGameRO(ro)));
      }
}
