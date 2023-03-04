import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Bot } from 'src/app/bots/store';
import { ApiService } from 'src/app/core/service';

export class BotDataRO {
  'nick-name' : string = "";
  'owner-ref' : string = "";
  'api-key' : string = "";
  init(bot : Bot)
  {
    this['owner-ref'] = bot.ownerRef;
    this['nick-name'] = bot.name;
    this['api-key'] = bot.apiKey;
    return this;
  }
}
interface BotInfoRO {
  balance : number;
}

interface BotRO {
  refLocal : string;
  refGlobal : string;
  data : BotDataRO;
  info : BotInfoRO;
}

@Injectable({ providedIn: 'root' })
export class BotService {
  constructor(private apiService: ApiService) {}

  getBots(player : String): Observable<Bot[]> {
    return this.apiService
      .get<BotRO[]>(`/players/` + player + "/bots")
      .pipe(map(this.convertListBotRO));
  }

  getBot(name: string): Observable<Bot> {
    return this.apiService
      .get<BotRO>(`/bots/${name}`)
      .pipe(map(convertBotRO));
  }

  addBot(player: string, name: string, data : BotDataRO): Observable<{}> {
    return this.apiService
      .put(`/players/${player}/bots/${name}`, data);
  }

  updateBot(name: string, data : BotDataRO): Observable<{}> {
    return this.apiService
      .patch(`/bots/${name}`, data);
  }

  deleteBot(name: string): Observable<{}> {
    return this.apiService
      .delete(`/bots/${name}`);
  }

  convertListBotRO(ros : BotRO[]): Bot[]
  {
    return ros.map(convertBotRO);
  }
}

function convertBotRO(ro : BotRO): Bot
{
  return new Bot().init(ro.refGlobal, ro.refLocal, ro.data['owner-ref'], ro.data['nick-name'], ro.data['api-key']);
}
