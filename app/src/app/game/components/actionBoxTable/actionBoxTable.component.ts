import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

import { Seat } from 'src/app/seats/store/seat-models';

@Component({
  selector: 'actionBoxTable',
  templateUrl: './actionBoxTable.component.html',
  styleUrls: ['./actionBoxTable.component.sass']
})
export class ActionBoxTableComponent implements OnChanges {
  @Input() seat!: Seat;
  isOut : boolean = false;
  isIn : boolean = false;
  @Output() rebuy = new EventEmitter<Seat>();
  @Output() comeIn = new EventEmitter<Seat>();
  @Output() standUp = new EventEmitter<Seat>();
  @Output() leave = new EventEmitter<Seat>();

  ngOnChanges(changes: SimpleChanges): void {
    this.isOut = this.seat.isOutSeat;
    this.isIn = !this.seat.isOutSeat;
  }

  public onRebuy(): void {
    this.rebuy.emit(this.seat);
  }
  public onComeIn(): void {
    this.comeIn.emit(this.seat);
  }
  public onStandUp(): void {
    this.standUp.emit(this.seat);
  }
  public onLeave(): void {
    this.leave.emit(this.seat);
  }
}
