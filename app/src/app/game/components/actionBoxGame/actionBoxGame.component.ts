import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, Unsubscribable } from 'rxjs';
import { last } from 'rxjs/operators';
import { SeatState, selectAllSeats } from 'src/app/seats';

import { Raise, Seat } from 'src/app/seats/store/seat-models';
import { Game, GameState, selectGame } from '../../store';

@Component({
  selector: 'actionBoxGame',
  templateUrl: './actionBoxGame.component.html',
  styleUrls: ['./actionBoxGame.component.sass']
})
export class ActionBoxGameComponent implements OnChanges {
  @Input() seat!: Seat;
  isOut : boolean = false;
  isIn : boolean = false;
  hasAction : boolean = false;

  @Output() fold = new EventEmitter<Seat>();
  @Output() check = new EventEmitter<Seat>();
  @Output() call = new EventEmitter<Seat>();
  @Output() raise = new EventEmitter<Raise>();

  unsubscribeOnDestroy : Unsubscribable[] = [];
  game$: Observable<Game>;
  allSeats$: Observable<Seat[]>;
  game?: Game;
  seats? : Seat[];
  raiseMin : number = 0;
  raiseHalfPotSize : number = 0;
  raiseTwoThirdPotSize : number = 0;
  raisePotSize : number = 0;
  raiseToPotSize : number = 0;
  raiseToPotAll : number = 0; // all players have more than half pot to pay
  raiseAllIn : number = 0;
  callSize : number = 0;

  constructor(
    private readonly seatStore: Store<SeatState>,
    private readonly gameStore: Store<GameState>,
  ) {
    this.allSeats$ = this.seatStore.select(selectAllSeats);
    this.game$ = this.gameStore.select(selectGame);
  }

  ngOnInit(): void {
    this.unsubscribeOnDestroy.push(this.allSeats$.subscribe(allSeats => {
      if (allSeats !== undefined && allSeats.length > 0)
      {
        this.seats = allSeats;
      }
      if (this.game !== undefined && this.seats !== undefined)
      {
        this.updateActions(this.seats, this.game);
      }
    })); 
    this.unsubscribeOnDestroy.push(this.game$.subscribe(game => {
      this.game = game;
      if (this.seats !== undefined)
      {
        this.updateActions(this.seats, this.game);
      }
    })); 
  };

  updateActions(seats: Seat[], game: Game) : void {
    this.callSize = this.getCallSize(seats);
    this.raiseMin = this.getMinRaise(seats, game);
    this.raiseHalfPotSize = this.ceilRaise(this.getPotSize(seats, game) * 0.5, game, this.raiseMin);
    this.raiseTwoThirdPotSize = this.ceilRaise(this.getPotSize(seats, game) * 0.66, game, this.raiseMin);
    this.raisePotSize = this.ceilRaise(this.getPotSize(seats, game), game, this.raiseMin);
    this.raiseToPotSize = this.ceilRaise(this.getToPotSize(seats, game), game, this.raiseMin);
    this.raiseToPotAll = this.ceilRaise(this.getToPotAll(seats, game), game, this.raiseMin);
    this.raiseAllIn = this.seat.coinsOnSeat;
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.isOut = this.seat.isOutGame;
    this.isIn = !this.seat.isOutGame;
    this.hasAction = this.seat.hasAction;
  }

  getCallSize(seats : Seat[]) : number {
    var lastBet : number = 0;
    seats.forEach(seat => {
      if (seat.coinsInPlay > 0)
      {
        if (seat.coinsInPlay > lastBet)
        {
          lastBet = seat.coinsInPlay;
        }
      }
    });
    // if the call is more than all in than the call must be reduced
    var result = lastBet - this.seat.coinsInPlay;
    if (result > this.seat.coinsOnSeat)
    {
      result = this.seat.coinsOnSeat
    }
    return result;
  }

  /**
   * @returns minimal raise - is minimal the difference of the last raise or big blind
   */
  getMinRaise(seats : Seat[], game : Game) : number {
    var lastBet : number = 0;
    var previousBet : number = 0;
    seats.forEach(seat => {
      if (seat.coinsInPlay > 0)
      {
        if (seat.coinsInPlay > lastBet)
        {
          previousBet = lastBet;
          lastBet = seat.coinsInPlay;
        } else if (seat.coinsInPlay > previousBet)
        {
          previousBet = seat.coinsInPlay;
        }
      }
    });
    var result : number = Math.ceil((lastBet + (lastBet - previousBet)) / game.coinsBigBlind) * game.coinsBigBlind;
    if (result < game.coinsBigBlind) {
      result = game.coinsBigBlind;
    }
    return result;
  }

  ceilRaise(raise: number, game : Game, minRaise : number) : number {
    var result = Math.ceil(raise / game.coinsBigBlind) * game.coinsBigBlind;
    if (result < minRaise) {
      result = minRaise;
    }
    return result;
  }
  getPotSize(seats : Seat[], game : Game) : number {
    var result : number = game.coinsPot;
    seats.forEach(seat => {
      result += seat.coinsInPlay;
    });
    return result;
  }
  getToPotSize(seats : Seat[], game : Game) : number {
    var lastBet : number = 0;
    seats.forEach(seat => {
      if (seat.coinsInPlay > 0)
      {
        if (seat.coinsInPlay > lastBet)
        {
          lastBet = seat.coinsInPlay;
        }
      }
    });
    return game.coinsPot + 3 * lastBet;
  }
  getToPotAll(seats : Seat[], game : Game) : number {
    var lastBet : number = 0;
    var playerWithBets : number = 0;
    seats.forEach(seat => {
      if (seat.coinsInPlay > 0)
      {
        if (seat.coinsInPlay > lastBet)
        {
          lastBet = seat.coinsInPlay;
        }
        if (!seat.isallin) //  && !seat.hasFolded)
        {
          playerWithBets ++;
        }
      }
    });
    return game.coinsPot + (3 * lastBet * playerWithBets);
  }

  public onFold(): void {
    this.fold.emit(this.seat);
  }
  public onCheck(): void {
    this.check.emit(this.seat);
  }
  public onCall(): void {
    this.call.emit(this.seat);
  }
  public onRaiseMin(): void {
    var betTo : number = this.raiseMin;
    this.raise.emit(new Raise().init(this.seat, betTo));
  }
  public onRaiseHalf(): void {
    var betTo = this.raiseHalfPotSize;
    this.raise.emit(new Raise().init(this.seat, betTo));
  }
  public onRaiseTwoThird(): void {
    var betTo = this.raiseTwoThirdPotSize;
    this.raise.emit(new Raise().init(this.seat, betTo));
  }
  public onRaisePot(): void {
    var betTo : number = this.raisePotSize;
    this.raise.emit(new Raise().init(this.seat, betTo));
  }
  public onRaiseToPotSize(): void {
    var betTo : number = this.raiseToPotSize;
    this.raise.emit(new Raise().init(this.seat, betTo));
  }
  public onRaiseToPotAll(): void {
    var betTo : number = this.raiseToPotAll;
    this.raise.emit(new Raise().init(this.seat, betTo));
  }
  public onRaiseAllIn(): void {
    var betTo : number = this.raiseAllIn;
    this.raise.emit(new Raise().init(this.seat, betTo));
  }
}
