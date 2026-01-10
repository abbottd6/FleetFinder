import { Component } from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";

@Component({
  selector: 'app-how-to',
  standalone: false,
  templateUrl: './how-to.component.html',
  styleUrl: './how-to.component.css'
})
export class HowToComponent {
  constructor(protected auth: AuthService) {}
}
