import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { PlayerService } from './player.service';
import { PlayerState, addPlayers } from 'src/app/players/store';
import { ServiceEndpoint, ServiceState, updateServiceLocation } from 'src/app/core/service';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class PlayerFacade {
  arePlayersLoaded : boolean = false;
  areDependenciesLoading : boolean = false;
  constructor(
    private readonly store: Store<PlayerState>,
    private readonly service : PlayerService,
    private readonly serviceStore: Store<ServiceState>,
  ) {
  }

  loadPlayers() {
    if (this.arePlayersLoaded)
    {
      return;
    }
    // console.log("loadPlayers environment.serviceEndPoint", environment.serviceEndPoint);
    this.serviceStore.dispatch(updateServiceLocation(
      { serviceEndpoint : new ServiceEndpoint().init(environment.serviceEndPoint)}));
    this.arePlayersLoaded = true;
    this.service.getPlayers()
      .subscribe(players => this.store.dispatch(addPlayers({ players : players})));
  }
}
