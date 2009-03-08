import { Component, EventEmitter, Input, Output} from '@angular/core';
import { Observable } from 'rxjs';

import { Player } from 'src/app/players/store';

@Component({
  selector: 'listPlayers',
  templateUrl: './listPlayers.component.html',
  styleUrls: ['./listPlayers.component.sass']
})

export class ListPlayersComponent {
  @Input() players$!: Observable<readonly Player[]>;
  @Input() selectedPlayer? : Player;
  @Output() selectPlayer = new EventEmitter<Player>();

  public select(p: Player): void {
    this.selectPlayer.emit(p);
  }
}
