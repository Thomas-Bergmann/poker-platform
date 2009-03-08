import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Game } from '../../store';

@Component({
  selector: 'gameBoardCoins',
  templateUrl: './gameBoardCoins.component.html',
  styleUrls: ['./gameBoardCoins.component.sass']
})
export class GameBoardCoinsComponent implements OnChanges {
  @Input() game!: Game;

  ngOnChanges(changes: SimpleChanges): void {
  }
}

