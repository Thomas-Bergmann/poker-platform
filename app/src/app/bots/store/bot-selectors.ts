import { createSelector, createFeatureSelector } from '@ngrx/store';

import { Bot } from './bot-models';
import { botFeatureKey, BotState } from './bot-reducers';

const selectFeature = createFeatureSelector<BotState>(botFeatureKey);

export const selectBots = createSelector(selectFeature, getBotsFromState);
function getBotsFromState (state: BotState) : ReadonlyArray<Bot>
{
  var result: Array<Bot> = new Array();
  state.bots.forEach(p => result.push(p))
  return result;
}
