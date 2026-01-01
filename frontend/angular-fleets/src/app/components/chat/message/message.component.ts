import {Component, Input} from '@angular/core';
import {MessageViewModel} from "../../../models/chat/message-view-model";

@Component({
  selector: 'app-message',
  standalone: true,
  templateUrl: './message.component.html',
  styleUrl: './message.component.css'
})
export class MessageComponent {
  @Input() msg!: MessageViewModel;
}
