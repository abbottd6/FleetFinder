import {Component, OnDestroy, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth/auth-services/auth.service";
import {UserService} from "../../services/user-services/user.service";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";
import {filter, map, Observable, shareReplay, Subject, Subscription, take, takeUntil} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {WsGatewayService} from "../../services/websocket-messaging/ws-gateway.service";
import {MatBadgePosition} from "@angular/material/badge";
import {LayoutMode} from "../input-fields/search-bar/search-bar.component";
import {BreakpointObserver} from "@angular/cdk/layout";
import {NotificationService} from "../../services/facade-services/notifications/notification.service";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.css',
  standalone: false
})
export class NavBarComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  badgePosition: MatBadgePosition = "above after";
  private breakpointObserver = new BreakpointObserver();

  private wsConnectSub: Subscription | null = null;

  constructor(public userService: UserService,
              protected auth: AuthService,
              private chatHostSrv: ChatHostService,
              protected ws: WsGatewayService,
              private http: HttpClient,
              private notificationService: NotificationService) {
  }

  ngOnInit() {
    this.auth.isLoggedIn$.pipe(takeUntil(this.destroy$))
      .subscribe(isLoggedIn => {
        if(isLoggedIn && (this.userService.sessionUser === null)) {
          this.userService.refreshUser();
          this.notificationService.loadNotifications();
        }
      });

    this.wsConnectSub = this.ws.isConnected$.pipe(
      takeUntil(this.destroy$),
      filter(Boolean)
      ).subscribe(() => {
        this.wsConnectSub?.unsubscribe();
        this.wsConnectSub = null;
        this.pingForNotificationCount();
      });
  }

  pingForNotificationCount() {
    this.ws.publish('/app/system.notify/get_unread',
      null)
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

  onOpenChange(open: boolean) {
    this.notificationService.setOpenState(open);
  }

  testUnreadPush() {
    this.http.post('/api/ws-test/unread', {}).subscribe();
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

  navbarLayoutMode$: Observable<LayoutMode> = this.breakpointObserver
    .observe([
      '(max-width: 1200px)',
      '(min-width: 1251px)'
    ])
    .pipe(
      map(state => {
        if (state.breakpoints['(max-width: 1200px)']) {
          return 'handheld';
        }

        return 'full';
      }),
      shareReplay(1)
    );
}
