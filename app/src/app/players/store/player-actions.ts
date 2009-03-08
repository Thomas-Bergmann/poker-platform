import { createAction, props } from '@ngrx/store';
import { Player } from './player-models';

export const addPlayers = createAction(
  'Add Players',
  props<{ players: Player[] }>()
);
