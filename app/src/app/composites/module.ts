import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import * as composites from './components';

import { TableModule } from '../tables';
import { SeatModule } from '../seats';
import { PlayerModule } from '../players';
import { CompositeRoutingModule } from './routing';
import { GameModule } from '../game';
import { BotModule } from '../bots';


@NgModule({
  declarations: [
    composites.ListPlayersViewComponent,
    composites.DetailPlayerViewComponent,
    composites.ListTablesViewComponent,
    composites.DetailTableViewComponent,
    composites.GameTableViewComponent,
  ],
  imports: [
    CommonModule,
    FormsModule, ReactiveFormsModule, HttpClientModule,
    CompositeRoutingModule,
    PlayerModule,
    BotModule,
    TableModule,
    SeatModule,
    GameModule,
  ],
  providers: [],
  exports: []
})

export class CompositeModule {}
