import { NgModule } from '@angular/core';
import { StoreModule } from '@ngrx/store';
import * as fromReducers from './service-reducers';

@NgModule({
  imports: [
    StoreModule.forFeature(fromReducers.serviceFeatureKey, fromReducers.serviceReducer)
  ],
})

export class ServiceModule {}
