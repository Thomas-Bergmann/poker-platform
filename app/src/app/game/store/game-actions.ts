import { createAction, props } from '@ngrx/store';
import { Game } from './game-models';

export const reload = createAction(
  'Reload',
  props<{ game: Game }>()
);
