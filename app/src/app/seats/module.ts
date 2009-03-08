import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import * as store from './store';
import * as service from './service';
import * as component from './components';
import * as players from '../players';

@NgModule({
  declarations: [
    component.AddSeatComponent,
    component.CardSeatComponent,
    component.ListSeatsComponent,
  ],
  imports: [
    CommonModule,
    FormsModule, ReactiveFormsModule, HttpClientModule,
    StoreModule.forFeature(store.seatFeatureKey, store.seatReducer),
    players.PlayerModule,
  ],
  providers: [
    service.SeatFacade, 
    service.SeatService,
  ],
  exports: [
    component.ListSeatsComponent,
    component.AddSeatComponent,
  ]
})

export class SeatModule {}
