import {Component, Input} from '@angular/core';
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";
import {MatIcon} from "@angular/material/icon";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-chat-panel',
  standalone: true,
  templateUrl: './chat-panel.component.html',
  imports: [
    MatIcon
  ],
  styleUrl: './chat-panel.component.css'
})
export class ChatPanelComponent {


  @Input() open!: boolean;



  constructor(private chatHostSrv: ChatHostService) {}

  terminateChat(){
    this.chatHostSrv.closeChat()
  }

  toggleWindowState() {
    this.chatHostSrv.toggleWindowState();
  }
}
