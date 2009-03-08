import { Action, createReducer, on } from '@ngrx/store';
import { Game } from './game-models';
import { reload } from './game-actions';

export const gameFeatureKey = 'gameState';

// keys tableid@playerid and gameid
var initialGame : Game = new Game().initForEmtpy();
export interface GameState {
  game: Game;
}

const initialState : GameState = {
  game : initialGame,
}

const _gameReducer = createReducer(
  initialState,
  on(reload, (state, action) => _updateGamesAtState(state, action.game)),
);

export function gameReducer(state: GameState | undefined, action: Action) {
  return _gameReducer(state, action);
}

function _updateGamesAtState(state:GameState, newGame: Game):GameState
{
  return ({
    ...state,
    game: newGame,
  });
}
