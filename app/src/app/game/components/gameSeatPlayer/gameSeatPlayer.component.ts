import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { Seat } from 'src/app/seats/store/seat-models';
@Component({
  selector: 'gameSeatPlayer',
  templateUrl: './gameSeatPlayer.component.html',
  styleUrls: ['./gameSeatPlayer.component.sass']
})
export class GameSeatPlayerComponent implements OnChanges {
  @Input() seat!: Seat;

  ngOnChanges(changes: SimpleChanges): void {
  }
}

