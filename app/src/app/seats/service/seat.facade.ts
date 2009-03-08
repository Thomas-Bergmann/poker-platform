import { Store } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { SeatService } from './seat.service';
import { SeatState, Seat, addSeats, addSeat, removeSeat, updateSeat, updateSeats } from 'src/app/seats/store';
import { ServiceEndpoint, ServiceState, updateServiceLocation } from 'src/app/core/service';
import { environment } from 'src/environments/environment';
import { Table } from '../../tables';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SeatFacade {
  areSeatsLoaded : Map<string, boolean> = new Map();
  constructor(
    private readonly store: Store<SeatState>,
    private readonly service : SeatService,
    private readonly serviceStore: Store<ServiceState>,
  ) {
  }

  loadSeats(table : Table) {
    if (this.areSeatsLoaded.get(table.resourceURI))
    {
      return;
    }
    // console.log("loadSeats seat.serviceEndPoint", environment.serviceEndPoint, table.resourceURI);
    this.serviceStore.dispatch(updateServiceLocation(
      { serviceEndpoint : new ServiceEndpoint().init(environment.serviceEndPoint)}));
    this.areSeatsLoaded.set(table.resourceURI, true);
    this.service.getSeats(table)
      .subscribe(seats => {
        this.store.dispatch(addSeats({ seats : seats}));
        this.areSeatsLoaded.delete(table.resourceURI);
      });
  }
  addSeat(seat:Seat) : Observable<{}> {
    this.store.dispatch(addSeat({ seat : seat}));
    return this.service.addSeat(seat);
  }
  updateSeat(seat: Seat) {
    this.service.updateSeat(seat).subscribe(seatRo => {
      this.store.dispatch(updateSeat({ seat : seat}));
    });
  }
  deleteSeat(seat: Seat) {
    this.service.deleteSeat(seat).subscribe(seatRo => {
      this.store.dispatch(removeSeat({ seat : seat}));
    });
  }
  updatedSeats(seats: Seat[]) {
    this.store.dispatch(updateSeats({ seats : seats}));
  }
}
