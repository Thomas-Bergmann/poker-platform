import { createSelector, createFeatureSelector } from '@ngrx/store';

import { Seat } from './seat-models';
import { seatFeatureKey, SeatState } from './seat-reducers';

const selectFeature = createFeatureSelector<SeatState>(seatFeatureKey);

export const selectAllSeats = createSelector(selectFeature, getAllSeatsFromState);
function getAllSeatsFromState (state: SeatState) : Seat[]
{
  return state.seats;
}
