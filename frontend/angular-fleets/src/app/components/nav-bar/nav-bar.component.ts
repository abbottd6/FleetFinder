import {AfterViewInit, Component} from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {UserService} from "../../services/user-services/user.service";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";

@Component({
    selector: 'app-nav-bar',
    templateUrl: './nav-bar.component.html',
    styleUrl: './nav-bar.component.css',
    standalone: false
})
export class NavBarComponent {

  constructor(public userService: UserService,
              protected auth: AuthService,
              private chatHostSrv: ChatHostService) {}

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



  toggleChat() {
    this.chatHostSrv.toggleChat();
  }
}
