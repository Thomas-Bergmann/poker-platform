import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { Seat } from 'src/app/seats/store/seat-models';
@Component({
  selector: 'gameSeat',
  templateUrl: './gameSeat.component.html',
  styleUrls: ['./gameSeat.component.sass']
})
export class GameSeatComponent implements OnChanges {
  @Input() seat!: Seat;
  @Input() selectedSeat?: Seat;
  isOut : boolean = false;
  isIn : boolean = false;

  ngOnChanges(changes: SimpleChanges): void {
    this.isOut = this.seat.isOutGame;
    this.isIn = !this.seat.isOutGame;
  }
}

