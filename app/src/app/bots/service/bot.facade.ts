import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { BotService, BotDataRO } from './bot.service';
import { BotState, Bot, addBots, addBot, updateBot } from 'src/app/bots/store';
import { ServiceEndpoint, ServiceState, updateServiceLocation } from 'src/app/core/service';
import { environment } from 'src/environments/environment';
import { Player } from 'src/app/players';

@Injectable({ providedIn: 'root' })
export class BotFacade {
  areBotsLoading : Player[] = [];
  constructor(
    private readonly store: Store<BotState>,
    private readonly service : BotService,
    private readonly serviceStore: Store<ServiceState>,
  ) {
    this.serviceStore.dispatch(updateServiceLocation(
      { serviceEndpoint : new ServiceEndpoint().init(environment.serviceEndPoint)}));
  }

  loadBots(player : Player) {
    if (this.areBotsLoading.indexOf(player) > -1)
    {
      return;
    }
    // console.log("loadBots environment.serviceEndPoint", environment.serviceEndPoint);
    this.areBotsLoading.push(player);
    this.service.getBots(player.localRef)
      .subscribe(bots => {
        this.store.dispatch(addBots({ bots : bots}));
        const index = this.areBotsLoading.indexOf(player, 0);
        if (index > -1) {
          this.areBotsLoading.splice(index, 1);
        }
      });
  }
  addBot(player: Player, bot:Bot) {
    var data : BotDataRO = new BotDataRO().init(bot);
    this.store.dispatch(addBot({ bot : bot}));
    this.service.addBot(player.localRef, bot.name, data).subscribe();
  }
  updateBot(bot: Bot) {
    var data : BotDataRO = new BotDataRO().init(bot);
    this.store.dispatch(updateBot({ bot : bot}));
    this.service.updateBot(bot.name, data).subscribe();
  }
}
