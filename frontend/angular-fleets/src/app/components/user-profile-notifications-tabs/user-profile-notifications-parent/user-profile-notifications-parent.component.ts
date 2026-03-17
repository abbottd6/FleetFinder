import { Component } from '@angular/core';
import {MatTabsModule} from "@angular/material/tabs";
import {
  ProfileNotificationSettingsTabComponent
} from "../profile-notification-settings-tab/profile-notification-settings-tab.component";
import {ProfileNotificationsTabComponent} from "../profile-notifications-tab/profile-notifications-tab.component";

@Component({
  selector: 'app-user-profile-notifications',
  standalone: true,
  templateUrl: './user-profile-notifications-parent.component.html',
  imports: [
    MatTabsModule,
    ProfileNotificationSettingsTabComponent,
    ProfileNotificationsTabComponent
  ],
  styleUrl: './user-profile-notifications-parent.component.css'
})
export class UserProfileNotificationsParentComponent {

}
