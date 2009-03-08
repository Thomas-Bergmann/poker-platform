import { Component, OnInit} from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable, Unsubscribable } from 'rxjs';

import { PlayerState, Player, selectPlayers } from 'src/app/players/store';
import { PlayerFacade } from 'src/app/players/service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'listPlayersView',
  templateUrl: './listPlayersView.component.html',
  styleUrls: ['./listPlayersView.component.sass']
})

export class ListPlayersViewComponent implements OnInit  {
  players$: Observable<readonly Player[]>;
  unsubscribeOnDestroy : Unsubscribable[] = [];

  constructor(
    private readonly playerStore: Store<PlayerState>,
    private readonly playerFacade: PlayerFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {
    this.players$ = this.playerStore.select(selectPlayers);
  }
  ngOnInit(): void {
    this.playerFacade.loadPlayers();
  }
  ngOnDestroy() {
    this.unsubscribeOnDestroy.forEach(s => s.unsubscribe());
  }

  public onSelectPlayer(p: Player): void {
    this.router.navigate([p.name], { relativeTo: this.route.parent });
  }
}
