import { Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, Unsubscribable } from 'rxjs';

import { PlayerState, Player, selectPlayers } from 'src/app/players/store';
import { PlayerFacade } from 'src/app/players/service';
import { ActivatedRoute, Router } from '@angular/router';
import { Bot, BotState, BotFacade, selectBots } from 'src/app/bots';

@Component({
  selector: 'detailPlayerView',
  templateUrl: './detailPlayerView.component.html',
  styleUrls: ['./detailPlayerView.component.sass']
})

export class DetailPlayerViewComponent implements OnInit  {
  players$: Observable<readonly Player[]>;
  unsubscribeOnDestroy : Unsubscribable[] = [];

  routePlayer? : string;
  selectedPlayer? : Player;
  bots?: readonly Bot[] = undefined;
  players?: readonly Player[] = undefined;

  constructor(
    private readonly playerStore: Store<PlayerState>,
    private readonly playerFacade: PlayerFacade,
    private readonly botStore: Store<BotState>,
    private readonly botFacade: BotFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    this.players$ = this.playerStore.select(selectPlayers);
  }
  ngOnInit(): void {
    this.unsubscribeOnDestroy.push(this.route.params.subscribe(params => {
      this.routePlayer = params['player'];
      this.selectedPlayer = undefined;
      this.bots = undefined;
      this.sureThatEveryThingIsLoaded();
    }));
    this.unsubscribeOnDestroy.push(this.players$.subscribe(players => {
      this.players = players;
      this.sureThatEveryThingIsLoaded();
    }));
    this.unsubscribeOnDestroy.push(this.botStore.select(selectBots).subscribe(bots => {
      this.bots = bots;
      this.sureThatEveryThingIsLoaded();
    }));
  }
  ngOnDestroy() {
    this.unsubscribeOnDestroy.forEach(s => s.unsubscribe());
  }

  public onSelectPlayer(p: Player): void {
    this.router.navigate([p.name], { relativeTo: this.route.parent?.parent });
  }

  private sureThatEveryThingIsLoaded() {
    this.makeSurePlayersAreLoaded();
    this.makeSureBotsAreLoaded();
  }

  private makeSurePlayersAreLoaded() {
    if (this.players ===  undefined)
    {
      this.playerFacade.loadPlayers();
      return;
    }
    this.players
    .filter(p => this.routePlayer == p.name)
    .forEach(p => {
      if (this.selectedPlayer !== p) {
        this.selectedPlayer = p;
        this.bots = undefined;
        this.sureThatEveryThingIsLoaded();
      }
  });

  }
  private makeSureBotsAreLoaded() {
    if (this.selectedPlayer ===  undefined)
    {
      return;
    }
    if (this.bots === undefined)
    {
      this.botFacade.loadBots(this.selectedPlayer);
      return;
    }
  }
}
