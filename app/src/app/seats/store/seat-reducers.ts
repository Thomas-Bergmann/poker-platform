import { Action, createReducer, on } from '@ngrx/store';
import { Seat } from './seat-models';
import { addSeats, addSeat, removeSeat, updateSeat, updateSeats } from './seat-actions';

export const seatFeatureKey = 'seatState';

export interface SeatState {
  seats: Seat[];
}

const initialState : SeatState = {
  seats : [],
}

const _seatReducer = createReducer(
  initialState,
  on(addSeats, (state, action) => _updateSeatsAtState(state, _setSeats(state.seats, action.seats))),
  on(addSeat, (state, action) => _updateSeatsAtState(state, _setSeat(state.seats, action.seat))),
  on(removeSeat, (state, action) => _updateSeatsAtState(state, _removeSeat(state.seats, action.seat))),
  on(updateSeat, (state, action) => _updateSeatsAtState(state, _setSeat(state.seats, action.seat))),
  on(updateSeats, (state, action) => _updateSeatsAtState(state, _setSeats(state.seats, action.seats))),
);

export function seatReducer(state: SeatState | undefined, action: Action) {
  return _seatReducer(state, action);
}

function _updateSeatsAtState(state:SeatState, newSeats: Seat[]):SeatState
{
  return ({
    ...state,
    seats: newSeats,
  });
}

function _getKey(seat : Seat):String
{
  return seat.resourceURI;
}

function _setSeat(seats:Seat[],  seat:Seat):Seat[]
{
  let newTableSeats : Map<String, Seat> = new Map();
  seats.forEach(s => newTableSeats.set(_getKey(s), s));
  newTableSeats.set(_getKey(seat), seat);
  return Array.from(newTableSeats.values());
}

function _setSeats(seats:Seat[],  setSeats:Seat[]):Seat[]
{
  let newSeats : Seat[] = seats;
  setSeats.forEach(seat => {
    newSeats = _setSeat(newSeats, seat);
  });
  return newSeats;
}

function _removeSeat(seats : Seat[], seat:Seat):Seat[]
{
  let newTableSeats : Map<String, Seat> = new Map();
  seats.forEach(s => newTableSeats.set(_getKey(s), s));
  newTableSeats.delete(_getKey(seat));
  return Array.from(newTableSeats.values());
}
