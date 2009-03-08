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
    component.AddBotComponent,
    component.CardBotComponent,
    component.ListBotsComponent,
  ],
  imports: [
    CommonModule,
    FormsModule, ReactiveFormsModule, HttpClientModule,
    StoreModule.forFeature(store.botFeatureKey, store.botReducer),
  ],
  providers: [
    service.BotFacade, service.BotService,
  ],
  exports: [
    component.AddBotComponent,
    component.ListBotsComponent,
  ]
})

export class BotModule {}
