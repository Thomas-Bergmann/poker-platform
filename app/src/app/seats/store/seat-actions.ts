import { createAction, props } from '@ngrx/store';
import { Seat } from './seat-models';

export const addSeats = createAction(
  'Add Seats',
  props<{ seats: Seat[] }>()
);

export const addSeat = createAction(
  'Add Seat',
  props<{ seat: Seat }>()
);

export const removeSeat = createAction(
  'Remove Seat',
  props<{ seat: Seat }>()
);

export const updateSeat = createAction(
  'Update Seat',
  props<{ seat: Seat }>()
)

export const updateSeats = createAction(
  'Update Seats',
  props<{ seats: Seat[] }>()
)
