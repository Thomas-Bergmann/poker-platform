import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

import { Seat } from 'src/app/seats/store/seat-models';
@Component({
  selector: 'gameSeatState',
  templateUrl: './gameSeatState.component.html',
  styleUrls: ['./gameSeatState.component.sass']
})
export class GameSeatStateComponent implements OnChanges {
  @Input() seat!: Seat;
  isOut : boolean = false;
  isIn : boolean = false;
  onButton : boolean = false;
  isAllIn : boolean = false;
  hasAction: boolean = false;
  ngOnChanges(changes: SimpleChanges): void {
    this.isOut = this.seat.isOutSeat;
    this.isIn = !this.seat.isOutGame && !this.seat.onButton && !this.seat.isallin && !this.seat.hasAction;
    this.onButton = this.seat.onButton;
    this.isAllIn = this.seat.isallin;
    this.hasAction = this.seat.hasAction;
  }
}

