import { Component } from '@angular/core';
import {DropdownModule} from "../../dropdowns/dropdown-module/dropdown.module";

@Component({
  selector: 'app-custom-notification-form',
  standalone: true,
  templateUrl: './custom-notification-form.component.html',
  imports: [
    DropdownModule
  ],
  styleUrl: './custom-notification-form.component.css'
})
export class CustomNotificationFormComponent {

}
