import {Component, Input} from '@angular/core';
import {MessageViewModel} from "../../../models/chat/message-view-model";
import {UserService} from "../../../services/user-services/user.service";
import {DatePipe, NgClass} from "@angular/common";
import {MatHint} from "@angular/material/form-field";

@Component({
  selector: 'app-message',
  standalone: true,
  templateUrl: './message.component.html',
  imports: [
    NgClass,
    DatePipe,
  ],
  styleUrl: './message.component.css'
})
export class MessageComponent {
  @Input() msg!: MessageViewModel;

  constructor(private userService: UserService) {
    this.userIsSender;
  }

  get userIsSender() {
    if(!this.msg) return false;
    return this.msg.senderId === this.userService.userId;
  }
}
