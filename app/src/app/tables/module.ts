import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule} from '@angular/forms';
import * as store from './store';
import * as service from './service';
import * as component from './components';
import { PlayerModule } from '../players';

@NgModule({
  declarations: [
    component.CardTableComponent,
    component.ListTablesComponent,
  ],
  imports: [
    CommonModule,
    FormsModule, ReactiveFormsModule, HttpClientModule,
    StoreModule.forFeature(store.tableFeatureKey, store.tableReducer),
    PlayerModule,
  ],
  providers: [
    service.TableFacade, service.TableService,
  ],
  exports: [
    component.ListTablesComponent,
  ]
})

export class TableModule {}
