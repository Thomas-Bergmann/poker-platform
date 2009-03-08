import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { Seat } from 'src/app/seats/store/seat-models';
@Component({
  selector: 'cardSeat',
  templateUrl: './cardSeat.component.html',
  styleUrls: ['./cardSeat.component.sass']
})
export class CardSeatComponent implements OnChanges {
  @Input() seat!: Seat;
  @Input() selectedSeat?: Seat;
  isOut : boolean = false;
  isIn : boolean = false;

  ngOnChanges(changes: SimpleChanges): void {
    this.isOut = this.seat.isOutSeat;
    this.isIn = !this.seat.isOutSeat;
  }
}

