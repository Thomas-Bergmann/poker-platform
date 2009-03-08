import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import * as store from './store';
import * as service from './service';
import * as component from './components';

@NgModule({
  declarations: [
    component.CardPlayerComponent,
    component.ListPlayersComponent,
    component.ItemPlayerComponent,
    component.SelectPlayerComponent,
  ],
  imports: [
    CommonModule,
    FormsModule, ReactiveFormsModule, HttpClientModule,
    StoreModule.forFeature(store.playerFeatureKey, store.playerReducer),
  ],
  providers: [
    service.PlayerFacade, service.PlayerService,
  ],
  exports: [
    component.ListPlayersComponent,
    component.SelectPlayerComponent,
  ]
})

export class PlayerModule {}
