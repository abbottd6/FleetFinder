import { Component } from '@angular/core';

@Component({
    selector: 'app-nav-bar',
    templateUrl: './nav-bar.component.html',
    styleUrl: './nav-bar.component.css',
    standalone: false
})
export class NavBarComponent {

  closeDropdown() {
    const dropdown = document.getElementById('navbarNavDropdown');
    if (dropdown) {
      dropdown.setAttribute('aria-expanded', 'flase');
      dropdown.classList.remove('show');
      const menu =document.querySelector('.dropdown-menu');
      if (menu) {
        menu.classList.remove('show');
      }
    }
  }
}
