import { Component, EventEmitter, Input, Output} from '@angular/core';
import { Observable } from 'rxjs';

import { Bot } from 'src/app/bots/store';

@Component({
  selector: 'listBots',
  templateUrl: './listBots.component.html',
  styleUrls: ['./listBots.component.sass']
})

export class ListBotsComponent {
  @Input() bots!: readonly Bot[];
  @Input() selectedBot? : Bot;
  @Output() selectBot = new EventEmitter<Bot>();

  public select(p: Bot): void {
    this.selectBot.emit(p);
  }
}
