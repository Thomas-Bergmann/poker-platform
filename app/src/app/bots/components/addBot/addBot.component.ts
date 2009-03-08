import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';

import { OIDCState, selectCurrentUser } from 'src/app/oidc';
import { Bot, BotFacade } from 'src/app/bots';
import { Player } from 'src/app/players';

@Component({
  selector: 'addBot',
  templateUrl: './addBot.component.html',
  styleUrls: ['./addBot.component.sass']
})
export class AddBotComponent {
  nameField = '';
  @Input() player! : Player;

  constructor(
    private readonly botFacade: BotFacade,
  ) {
  }

  _addBot() {
    this.botFacade.addBot(
      this.player,
      new Bot().newBot(this.player.ownerRef, this.nameField));
      this.nameField = '';
  }
}
