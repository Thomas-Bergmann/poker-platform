import { Component, Input } from '@angular/core';

import { Bot } from 'src/app/bots/store/bot-models';
@Component({
  selector: 'cardBot',
  templateUrl: './cardBot.component.html',
  styleUrls: ['./cardBot.component.sass']
})
export class CardBotComponent {
  @Input() bot!: Bot;
  @Input() selectedBot?: Bot;
}
