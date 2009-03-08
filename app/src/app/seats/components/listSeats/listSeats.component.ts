import { Component, EventEmitter, Input, Output} from '@angular/core';

import { Seat } from 'src/app/seats/store';

@Component({
  selector: 'listSeats',
  templateUrl: './listSeats.component.html',
  styleUrls: ['./listSeats.component.sass']
})
export class ListSeatsComponent {
  @Input() seats!: readonly Seat[];
  @Input() selectedSeat? : Seat;
  @Output() selectSeat = new EventEmitter<Seat>();

  public select(p: Seat): void {
    this.selectSeat.emit(p);
  }
}
