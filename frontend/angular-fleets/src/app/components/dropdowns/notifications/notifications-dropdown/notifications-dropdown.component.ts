import { Component } from '@angular/core';
import {WsGatewayService} from "../../../../services/websocket-messaging/ws-gateway.service";
import {NotificationService} from "../../../../services/facade-services/notifications/notification.service";
import {AsyncPipe} from "@angular/common";
import {NotificationComponent} from "../notification/notification.component";

@Component({
  selector: 'app-notifications-dropdown',
  standalone: true,
  templateUrl: './notifications-dropdown.component.html',
  imports: [
    AsyncPipe,
    NotificationComponent,
  ],
  styleUrl: './notifications-dropdown.component.css'
})
export class NotificationsDropdownComponent {

  constructor(protected ws: WsGatewayService,
              protected noteService: NotificationService) {}
}
