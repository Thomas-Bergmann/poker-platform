import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { Seat } from 'src/app/seats/store/seat-models';
@Component({
  selector: 'gameSeatCoins',
  templateUrl: './gameSeatCoins.component.html',
  styleUrls: ['./gameSeatCoins.component.sass']
})
export class GameSeatCoinsComponent implements OnChanges {
  @Input() seat!: Seat;

  ngOnChanges(changes: SimpleChanges): void {
  }
}

