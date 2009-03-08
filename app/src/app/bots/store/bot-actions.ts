import { createAction, props } from '@ngrx/store';
import { Bot } from './bot-models';

export const addBots = createAction(
  'Add Bots',
  props<{ bots: Bot[] }>()
);

export const addBot = createAction(
  'Add Bot',
  props<{ bot: Bot }>()
);

export const removeBot = createAction(
  'Remove Bot',
  props<{ bot: Bot }>()
);

export const updateBot = createAction(
  'Update Bot',
  props<{ bot: Bot }>()
)
