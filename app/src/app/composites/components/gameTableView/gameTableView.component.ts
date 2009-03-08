import { Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, Unsubscribable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { TableFacade, TableState, Table, selectAllTables } from 'src/app/tables';
import { Raise, Seat, SeatFacade, SeatState, selectAllSeats } from 'src/app/seats';
import { PlayerState, selectPlayers, Player, PlayerFacade } from 'src/app/players';
import { Game, GameFacade, GameState, selectGame } from 'src/app/game';

@Component({
  selector: 'gameTableView',
  templateUrl: './gameTableView.component.html',
  styleUrls: ['./gameTableView.component.sass']
})

export class GameTableViewComponent implements OnInit  {
  allTables$: Observable<ReadonlyMap<string, Table>>;
  tables: readonly Table[] | undefined;
  allSeats$: Observable<Seat[]>;
  seats:  readonly Seat[]  | undefined;
  game$: Observable<Game>;
  allPlayers$: Observable<readonly Player[]>;
  players: readonly Player[] = [];
  unsubscribeOnDestroy : Unsubscribable[] = [];

  routeTable? : string;
  selectedTable? : Table;
  routeSeat? : number;
  selectedSeat? : Seat;
  selectedGame? : Game;
  interval?: number;
  doRefresh : boolean = true;

  constructor(
    private readonly tableStore: Store<TableState>,
    private readonly tableFacade: TableFacade,
    private readonly seatStore: Store<SeatState>,
    private readonly seatFacade: SeatFacade,
    private readonly playerStore: Store<PlayerState>,
    private readonly playerFacade: PlayerFacade,
    private readonly gameStore: Store<GameState>,
    private readonly gameFacade: GameFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    this.allTables$ = this.tableStore.select(selectAllTables);
    this.allSeats$ = this.seatStore.select(selectAllSeats);
    this.allPlayers$ = this.playerStore.select(selectPlayers);
    this.game$ = this.gameStore.select(selectGame);
  }
  ngOnChanges() : void {
    this.selectedTable = undefined;
    this.sureThatEveryThingIsLoaded();
  }
  ngOnInit(): void {
    this.unsubscribeOnDestroy.push(this.route.params.subscribe(params => {
      // console.log("route", params);
      this.routeTable = params['table'];
      this.routeSeat = params['seat'];
      this.selectedTable = undefined;
      this.selectedSeat = undefined;
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
    this.unsubscribeOnDestroy.push(this.game$.subscribe(game => {
      if (this.selectedGame === game)
      {
        return;
      }
      this.selectedGame = game;
      this.sureThatEveryThingIsLoaded();
    })); 
  }
  ngOnDestroy() {
    this.unsubscribeOnDestroy.forEach(s => s.unsubscribe());
    this.doRefresh = false;
  }

  public onSelectTable(p: Table): void {
    this.router.navigate([p.name], { relativeTo: this.route.parent?.parent?.parent });
  }
  public onSelectSeat(s: Seat): void {
    this.router.navigate([s.position], { relativeTo: this.route.parent });
  }
  public onComeIn(s:Seat): void {
    if (this.selectedTable?.maxRebuy !== undefined && s.coinsOnSeat < this.selectedTable?.maxRebuy)
    {
      this.seatFacade.updateSeat(s.sitOut(false).setCoinsOnSeat(this.selectedTable?.maxRebuy));
    }
  }
  public onStandUp(s:Seat): void {
    this.seatFacade.updateSeat(s.sitOut(true));
  }
  public onLeave(s:Seat): void {
    this.seatFacade.deleteSeat(s);
  }
  public onRebuy(s:Seat): void {
    if (this.selectedTable?.maxRebuy !== undefined)
    {
      this.seatFacade.updateSeat(s.setCoinsOnSeat(this.selectedTable?.maxRebuy));
    }
  }
  public onFold(s:Seat): void {
    this.gameFacade.doAction(s, "fold");
  }
  public onCheck(s:Seat): void {
    this.gameFacade.doAction(s, "check");
  }
  public onCall(s:Seat): void {
    this.gameFacade.doAction(s, "call");
  }
  public onRaise(r:Raise): void {
    this.gameFacade.doActionWithBetSize(r.seat, "raise", r.betTo);
  }

  private sureThatEveryThingIsLoaded() {
    this.makeSurePlayersAreLoaded();
    this.makeSureTableIsLoaded();
    this.makeSureSeatsAreLoaded();
    this.makeSureGameLoaded();
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
    this.seats
      .filter(s => this.routeSeat == s.position)
      .forEach(s => {
        if (this.selectedSeat !== s) {
          this.selectedSeat = s;
          this.sureThatEveryThingIsLoaded();
        }
    });
  }

  private makeSureGameLoaded() {
    if (this.selectedSeat === undefined || this.selectedTable === undefined)
    {
      return;
    }
    if (this.selectedGame === undefined || this.selectedGame.isEmpty())
    {
      this.gameFacade.loadGame(this.selectedSeat);
      this.interval = window.setTimeout(() => {
        this.reload(); // api call
     }, 5000);
    }
  }

  private reload() {
    if (this.selectedSeat !== undefined)
    {
      this.gameFacade.loadGame(this.selectedSeat);
      this.interval = window.setTimeout(() => {
        this.reload(); // api call
     }, 5000);
    }
  }
}

