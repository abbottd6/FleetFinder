import {AfterViewInit, Component} from '@angular/core';
import {ChatPanelComponent} from "./chat-panel/chat-panel.component";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";
import {AsyncPipe, NgIf} from "@angular/common";

export enum ChatWindowState {
  collapsed,
  expanded
}

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


  constructor(protected chatHostSrv: ChatHostService){}
}
