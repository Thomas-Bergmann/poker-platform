import { Action, createReducer, on } from '@ngrx/store';
import { updateServiceLocation } from './service-actions';
import { ServiceEndpoint, ServiceState } from './service-models';

export const serviceFeatureKey = 'serviceState';

var initialServices : ServiceEndpoint = new ServiceEndpoint().empty();

const initialState : ServiceState = {
  serviceEndpoint : initialServices,
  serviceErrors : []
}

const _serviceReducer = createReducer(
  initialState,
  on(updateServiceLocation, (state, action) => _updateEndpointAtState(state, action.serviceEndpoint)),
);

export function serviceReducer(state: ServiceState | undefined, action: Action) {
  return _serviceReducer(state, action);
}

function _updateEndpointAtState(state:ServiceState, serviceEndpoint: ServiceEndpoint):ServiceState
{
  return ({
    ...state,
    serviceEndpoint: serviceEndpoint,
  });
}
