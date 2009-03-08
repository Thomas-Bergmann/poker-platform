import { Component, EventEmitter, Input, Output} from '@angular/core';
import { Observable } from 'rxjs';

import { Player } from 'src/app/players/store';

@Component({
  selector: 'selectPlayer',
  templateUrl: './selectPlayer.component.html',
  styleUrls: ['./selectPlayer.component.sass']
})

export class SelectPlayerComponent {
  @Input() players!: readonly Player[];
  selectedPlayer? : Player;
  @Output('selectedPlayer') selectPlayer = new EventEmitter<Player>();

  public select(event: Player): void {
    // console.log("selected on SelectPlayerComponent", this.selectedPlayer);
    this.selectPlayer.emit(this.selectedPlayer);
  }
}
