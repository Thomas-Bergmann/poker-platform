import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { Seat } from 'src/app/seats/store/seat-models';
@Component({
  selector: 'gameSeatCards',
  templateUrl: './gameSeatCards.component.html',
  styleUrls: ['./gameSeatCards.component.sass']
})
export class GameSeatCardsComponent implements OnChanges {
  @Input() seat!: Seat;

  ngOnChanges(changes: SimpleChanges): void {
  }
}

