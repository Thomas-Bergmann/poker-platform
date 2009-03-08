import { createAction, props } from '@ngrx/store';
import { ServiceError, ServiceEndpoint} from '.';

export const serverError = createAction('Server Error', props<ServiceError>());
export const serverTimeout = createAction('Server Timeout');
export const clientError = createAction('Client Error', props<ServiceError>());

export const updateServiceLocation = createAction(
    'Update Service Endpoint',
    props<{ serviceEndpoint: ServiceEndpoint }>()
  )
