import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Game } from '../../store';

@Component({
  selector: 'gameBoardCards',
  templateUrl: './gameBoardCards.component.html',
  styleUrls: ['./gameBoardCards.component.sass']
})
export class GameBoardCardsComponent implements OnChanges {
  @Input() game!: Game;

  ngOnChanges(changes: SimpleChanges): void {
  }
}

