import {Component} from '@angular/core';
import {ChatPanelComponent} from "../chat-panel/chat-panel.component";
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";
import {AsyncPipe, NgIf} from "@angular/common";

export enum ChatWindowState {
  collapsed,
  expanded
}
export const { expanded } = ChatWindowState;
export const { collapsed } = ChatWindowState;

@Component({
  selector: 'app-chat-shell',
  standalone: true,
  templateUrl: './chat-shell.component.html',
  imports: [
    ChatPanelComponent,
    AsyncPipe,
    NgIf
  ],
  styleUrl: './chat-shell.component.css'
})
export class ChatShellComponent {
  ChatWindowState = ChatWindowState;

  constructor(protected chatHostSrv: ChatHostService){

  }
}
