import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ServiceEndpoint, ServiceState, updateServiceLocation } from 'src/app/core/service';
import { environment } from 'src/environments/environment';
import { Table } from '../../tables';
import { GameService } from './game.service';
import { GameState, Game, reload } from '../store';
import { Seat } from '../../seats';

@Injectable({ providedIn: 'root' })
export class GameFacade {
  areGamesLoaded : Map<string, boolean> = new Map();
  constructor(
    private readonly store: Store<GameState>,
    private readonly service : GameService,
    private readonly serviceStore: Store<ServiceState>,
  ) {
    this.serviceStore.dispatch(updateServiceLocation(
      { serviceEndpoint : new ServiceEndpoint().init(environment.serviceEndPoint)}));
  }

  loadGame(seat: Seat) {
    if (this.areGamesLoaded.get(seat.resourceURI))
    {
      return;
    }
    this.areGamesLoaded.set(seat.resourceURI, true);
    this.service.getCurrent(seat)
      .subscribe(game => {
        this.store.dispatch(reload({ game : game}));
        this.areGamesLoaded.delete(seat.resourceURI);
      });
  }
  doAction(seat: Seat, action: string)
  {
    this.doActionWithBetSize(seat, action, 0);
  }
  doActionWithBetSize(seat: Seat, action: string, betTo:number)
  {
    this.service.doAction(seat, action, betTo)
    .subscribe(game => {
      this.store.dispatch(reload({ game : game}));
      this.areGamesLoaded.delete(seat.resourceURI);
    });
  }
}
