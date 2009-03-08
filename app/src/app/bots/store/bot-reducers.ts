import { Action, createReducer, on } from '@ngrx/store';
import { Bot } from './bot-models';
import { addBots, addBot, removeBot, updateBot } from './bot-actions';

export const botFeatureKey = 'botState';

export interface BotState {
  bots: Bot[];
}

const initialState : BotState = {
  bots : [],
}

const _botReducer = createReducer(
  initialState,
  on(addBots, (state, action) => _updateBotsAtState(state, action.bots)),
  on(addBot, (state, action) => _updateBotsAtState(state, _setBot(state.bots, action.bot))),
  on(removeBot, (state, action) => _updateBotsAtState(state, _removeBot(state.bots, action.bot))),
  on(updateBot, (state, action) => _updateBotsAtState(state, _setBot(state.bots, action.bot))),
);

export function botReducer(state: BotState | undefined, action: Action) {
  return _botReducer(state, action);
}

function _updateBotsAtState(state:BotState, newBots: Bot[]):BotState
{
  return ({
    ...state,
    bots: newBots,
  });
}

function _setBot(bots:Bot[],  bot:Bot):Bot[]
{
  let newBots : Map<String, Bot> = new Map();
  bots.forEach(b => newBots.set(b.globalRef, b));
  newBots.set(bot.globalRef, bot);
  return Array.from(newBots.values());
}


function _removeBot(bots : Bot[], bot:Bot):Bot[]
{
  let newBots : Map<String, Bot> = new Map();
  bots.forEach(b => newBots.set(b.globalRef, b));
  newBots.delete(bot.localRef);
  return Array.from(newBots.values());
}
