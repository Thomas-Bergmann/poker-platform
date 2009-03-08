import { createSelector, createFeatureSelector } from '@ngrx/store';

import { Player } from './player-models';
import { playerFeatureKey, PlayerState } from './player-reducers';

const selectFeature = createFeatureSelector<PlayerState>(playerFeatureKey);

export const selectPlayers = createSelector(selectFeature, getPlayersFromState);
function getPlayersFromState (state: PlayerState) : ReadonlyArray<Player>
{
  var result: Array<Player> = new Array();
  state.players.forEach(p => result.push(p))
  return result;
}
