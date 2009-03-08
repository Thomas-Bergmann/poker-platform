import { Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, Unsubscribable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { TableFacade, TableState, Table, selectAllTables } from 'src/app/tables';
import { Seat, SeatFacade, SeatState, selectAllSeats } from 'src/app/seats';
import { PlayerState, selectPlayers, Player, PlayerFacade } from 'src/app/players';

@Component({
  selector: 'detailTableView',
  templateUrl: './detailTableView.component.html',
  styleUrls: ['./detailTableView.component.sass']
})

export class DetailTableViewComponent implements OnInit  {
  allTables$: Observable<ReadonlyMap<string, Table>>;
  tables: readonly Table[] | undefined;
  allSeats$: Observable<Seat[]>;
  seats:  readonly Seat[]  | undefined;
  allPlayers$: Observable<readonly Player[]>;
  players: readonly Player[] = [];
  unsubscribeOnDestroy : Unsubscribable[] = [];

  routeTable? : string;
  selectedTable? : Table;

  constructor(
    private readonly tableStore: Store<TableState>,
    private readonly tableFacade: TableFacade,
    private readonly seatStore: Store<SeatState>,
    private readonly seatFacade: SeatFacade,
    private readonly playerStore: Store<PlayerState>,
    private readonly playerFacade: PlayerFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    this.allTables$ = this.tableStore.select(selectAllTables);
    this.allSeats$ = this.seatStore.select(selectAllSeats);
    this.allPlayers$ = this.playerStore.select(selectPlayers);
  }
  ngOnChanges() : void {
    this.selectedTable = undefined;
    this.sureThatEveryThingIsLoaded();
  }
  ngOnInit(): void {
    this.unsubscribeOnDestroy.push(this.route.params.subscribe(params => {
      // console.log("route", params);
      this.routeTable = params['table'];
      this.selectedTable = undefined;
      this.sureThatEveryThingIsLoaded();
    }));
    this.unsubscribeOnDestroy.push(this.allPlayers$.subscribe(allPlayers => {
      this.players = allPlayers;
      this.sureThatEveryThingIsLoaded();
    })); 
    this.unsubscribeOnDestroy.push(this.allTables$.subscribe(allTables => {
      this.tables = Array.from(allTables?.values());
      this.sureThatEveryThingIsLoaded();
    })); 
    this.unsubscribeOnDestroy.push(this.allSeats$.subscribe(allSeats => {
      if (allSeats !== undefined && allSeats.length > 0)
      {
        this.seats = allSeats;
      }
      this.sureThatEveryThingIsLoaded();
    })); 
  }
  ngOnDestroy() {
    this.unsubscribeOnDestroy.forEach(s => s.unsubscribe());
  }

  public onSelectTable(p: Table): void {
    this.router.navigate([p.name], { relativeTo: this.route.parent?.parent });
  }
  public onSelectSeat(s: Seat): void {
    this.router.navigate(["seats", s.position], { relativeTo: this.route });
  }
  
  private sureThatEveryThingIsLoaded() {
    this.makeSurePlayersAreLoaded();
    this.makeSureTableIsLoaded();
    this.makeSureSeatsAreLoaded();
  }

  private makeSurePlayersAreLoaded() {
    if (this.players.length == 0)
    {
      this.playerFacade.loadPlayers();
      return;
    }
  }
  
  private makeSureTableIsLoaded() {
    if (this.tables === undefined)
    {
      this.tableFacade.loadTables();
      return;
    }
    this.tables
      .filter(p => this.routeTable == p.localRef)
      .forEach(p => {
        if (this.selectedTable !== p) {
          this.selectedTable = p;
          this.sureThatEveryThingIsLoaded();
        }
    });
  }
  private makeSureSeatsAreLoaded() {
    if (this.selectedTable === undefined)
    {
      return;
    }
    if (this.seats === undefined)
    {
      this.seatFacade.loadSeats(this.selectedTable);
      return;
    }
  }
}
