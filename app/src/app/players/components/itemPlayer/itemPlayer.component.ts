import { Component, Input } from '@angular/core';

import { Player } from 'src/app/players/store';
@Component({
  selector: 'itemPlayer',
  templateUrl: './itemPlayer.component.html',
  styleUrls: ['./itemPlayer.component.sass']
})
export class ItemPlayerComponent {
  @Input() player!: Player;
}
