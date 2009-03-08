import { Component, Input, OnChanges } from '@angular/core';

import { Game } from '../../store/game-models';

@Component({
  selector: 'gameBoard',
  templateUrl: './gameBoard.component.html',
  styleUrls: ['./gameBoard.component.sass']
})

export class GameBoardComponent {
  @Input() game!: Game;
}
