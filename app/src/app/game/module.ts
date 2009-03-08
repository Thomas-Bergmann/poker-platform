import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import * as store from './store';
import * as service from './service';
import * as component from './components';
import * as players from '../players';
import * as seats from '../seats';

@NgModule({
  declarations: [
    component.ActionBoxGameComponent,
    component.ActionBoxTableComponent,
    component.GameSeatComponent,
    component.GameSeatCardsComponent,
    component.GameSeatCoinsComponent,
    component.GameSeatStateComponent,
    component.GameSeatPlayerComponent,
    component.GameSeatRankComponent,
    component.GameBoardComponent,
    component.GameBoardCardsComponent,
    component.GameBoardCoinsComponent,
  ],
  imports: [
    CommonModule,
    FormsModule, ReactiveFormsModule, HttpClientModule,
    StoreModule.forFeature(store.gameFeatureKey, store.gameReducer),
    players.PlayerModule,
    seats.SeatModule,
  ],
  providers: [
    service.GameFacade, service.GameService, seats.SeatFacade
  ],
  exports: [
    component.GameSeatComponent,
    component.GameBoardComponent,
    component.ActionBoxGameComponent,
    component.ActionBoxTableComponent,
  ]
})

export class GameModule {}
