import { Component, inject } from '@angular/core';
import Keycloak from "keycloak-js";

@Component({
    selector: 'app-nav-bar',
    templateUrl: './nav-bar.component.html',
    styleUrl: './nav-bar.component.css',
    standalone: false
})
export class NavBarComponent {

  private kc = inject(Keycloak)

  login() {
    return this.kc.login();
  }

  logout() {
    return this.kc.logout({
      redirectUri: window.location.origin + ''
    });
  }

  register() {
    return this.kc.register();
  }

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
