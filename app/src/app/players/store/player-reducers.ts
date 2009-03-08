import { Action, createReducer, on } from '@ngrx/store';
import { Player } from './player-models';
import { addPlayers } from './player-actions';

export const playerFeatureKey = 'playerState';

export interface PlayerState {
  players: Player[];
}

const initialState : PlayerState = {
  players : [],
}

const _playerReducer = createReducer(
  initialState,
  on(addPlayers, (state, action) => _updatePlayersAtState(state, action.players)),
);

export function playerReducer(state: PlayerState | undefined, action: Action) {
  return _playerReducer(state, action);
}

function _updatePlayersAtState(state:PlayerState, newPlayers: Player[]):PlayerState
{
  return ({
    ...state,
    players: newPlayers,
  });
}
