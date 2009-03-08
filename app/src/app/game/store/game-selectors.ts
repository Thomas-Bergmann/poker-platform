import { createSelector, createFeatureSelector } from '@ngrx/store';

import { Game } from './game-models';
import { gameFeatureKey, GameState } from './game-reducers';

const selectFeature = createFeatureSelector<GameState>(gameFeatureKey);

export const selectGame = createSelector(selectFeature, getGameFromState);
function getGameFromState (state: GameState) : Game
{
  return state.game;
}
