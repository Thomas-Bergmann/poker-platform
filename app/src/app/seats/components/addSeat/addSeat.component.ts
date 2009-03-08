import { Observable } from 'rxjs';
import { Component, EventEmitter, Input, Output} from '@angular/core';

import { SeatFacade } from 'src/app/seats/service/seat.facade';
import { Seat } from 'src/app/seats/store';
import { Table } from 'src/app/tables';
import { Player } from 'src/app/players';

@Component({
  selector: 'addSeat',
  templateUrl: './addSeat.component.html',
  styleUrls: ['./addSeat.component.sass']
})
export class AddSeatComponent {
  @Input() players!: readonly Player[];
  @Input() table!: Table;
  @Output('selectedPlayer') selectPlayer = new EventEmitter<Player>();

  playerRef = '';
  constructor(
    private readonly seatFacade: SeatFacade
  ) {
  }
  _selectPlayer(event: Player): void {
    // console.log("selected on AddSeatComponent", event);
    this.playerRef = event.globalRef;
    this.selectPlayer.emit(event);
  }

  _addSeat() {
    // console.log("add seat at component");
    this.seatFacade.addSeat(
      new Seat().initForAdd(this.table.resourceURI, this.playerRef, this.table.maxRebuy)).subscribe(a => {
        this.seatFacade.loadSeats(this.table);
      });
      this.playerRef = '';
  }
}
