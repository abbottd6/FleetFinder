import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {UserService} from "../../services/user-services/user.service";

@Component({
    selector: 'app-nav-bar',
    templateUrl: './nav-bar.component.html',
    styleUrl: './nav-bar.component.css',
    standalone: false
})
export class NavBarComponent {

  constructor(public userService: UserService, protected auth: AuthService) {}

  closeDropdown() {
    const dropdown = document.getElementById('navbarNavDropdown');
    if (dropdown) {
      dropdown.setAttribute('aria-expanded', 'false');
      dropdown.classList.remove('show');
      const menu =document.querySelector('.dropdown-menu');
      if (menu) {
        menu.classList.remove('show');
      }
    }
  }
}
