import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { Seat } from 'src/app/seats/store/seat-models';
@Component({
  selector: 'gameSeatRank',
  templateUrl: './gameSeatRank.component.html',
  styleUrls: ['./gameSeatRank.component.sass']
})
export class GameSeatRankComponent implements OnChanges {
  @Input() seat!: Seat;

  ngOnChanges(changes: SimpleChanges): void {
  }
}

