import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {UserService} from "../../services/user-services/user.service";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";
import {Subject, takeUntil} from "rxjs";

@Component({
    selector: 'app-nav-bar',
    templateUrl: './nav-bar.component.html',
    styleUrl: './nav-bar.component.css',
    standalone: false
})
export class NavBarComponent implements OnDestroy {
  private destroy$ = new Subject<void>();

  constructor(public userService: UserService,
              protected auth: AuthService,
              private chatHostSrv: ChatHostService) {

    this.auth.isLoggedIn$.pipe(takeUntil(this.destroy$))
      .subscribe(isLoggedIn => {
        if(isLoggedIn && (this.userService.sessionUser === null)) {
          this.userService.refreshUser();
        }
      })
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

  navbarLogOut(){
    this.chatHostSrv.closeChat();
    this.auth.logout().subscribe();
  }

  toggleChat() {
    this.chatHostSrv.toggleChat();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
