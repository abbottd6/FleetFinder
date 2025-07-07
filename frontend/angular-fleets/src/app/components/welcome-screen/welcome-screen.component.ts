import { Component } from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";

@Component({
    selector: 'app-welcome-screen',
    templateUrl: './welcome-screen.component.html',
    styleUrl: './welcome-screen.component.css',
    standalone: false
})
export class WelcomeScreenComponent {
  constructor(public auth: AuthService) {}
}
