import { Component, Input } from '@angular/core';

import { Player } from 'src/app/players/store/player-models';
@Component({
  selector: 'cardPlayer',
  templateUrl: './cardPlayer.component.html',
  styleUrls: ['./cardPlayer.component.sass']
})
export class CardPlayerComponent {
  @Input() player!: Player;
  @Input() selectedPlayer?: Player;
}
