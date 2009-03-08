import { createSelector, createFeatureSelector } from '@ngrx/store';

import { ServiceState } from './service-models';
import { serviceFeatureKey } from './service-reducers';

const selectFeature = createFeatureSelector<ServiceState>(serviceFeatureKey);

export const restEndpoint = createSelector(
  selectFeature,
  (state: ServiceState) => state.serviceEndpoint.uri
);
