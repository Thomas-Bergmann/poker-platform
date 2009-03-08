import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import { OAuthModule } from 'angular-oauth2-oidc';
import { Router } from '@angular/router';
import { AppComponent } from './shell/components/app.component';
import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';

import * as games from './game';
import * as seats from './seats';
import * as bots from './bots';
import * as tables from './tables';
import * as players from './players';
import * as oidc from './oidc';
import * as core from './core/service/store';
import { CompositeRoutingModule } from './composites/routing';

@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule, ReactiveFormsModule, HttpClientModule,
    OAuthModule.forRoot(),
    StoreModule.forRoot({
      playerState: players.playerReducer,
      botState: bots.botReducer,
      tableState: tables.tableReducer,
      seatState: seats.seatReducer,
      gameState: games.gameReducer,
      oidcState : oidc.oidcReducer,
      serviceState : core.serviceReducer,
    }),
    games.GameModule,
    seats.SeatModule,
    tables.TableModule,
    players.PlayerModule,
    bots.BotModule,
    oidc.OIDCModule,
    core.ServiceModule,
    CompositeRoutingModule,
    AppRoutingModule,
    StoreDevtoolsModule.instrument({
      name: 'HATOKA Poker',
      maxAge: 50,
      logOnly: environment.production
    }),
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor(router: Router) {
  }
}
