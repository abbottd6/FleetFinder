import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatMenuModule } from '@angular/material/menu';
import { of, NEVER } from 'rxjs';
import { NavBarComponent } from './nav-bar.component';
import { UserService } from '../../services/user-services/user.service';
import { AuthService } from '../../services/auth/auth-services/auth.service';
import { ChatHostService } from '../../services/facade-services/chat/chat-host.service';
import { WsGatewayService } from '../../services/websocket-messaging/ws-gateway.service';
import { NotificationService } from '../../services/facade-services/notifications/notification.service';

describe('NavBarComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;

  beforeEach(async () => {
    const userSpy = jasmine.createSpyObj('UserService', ['refreshUser'], {
      sessionUser$: of(null), sessionUser: null, userId: 0
    });
    const authSpy = jasmine.createSpyObj('AuthService', ['logout'], { isLoggedIn$: of(false) });
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['toggleChat', 'openChatWith'], { open$: of(false) });
    const wsSpy = jasmine.createSpyObj('WsGatewayService', ['connect', 'disconnect'], {
      isConnected$: of(false), notifications$: of([]), notificationUnread$: of(0)
    });
    const noteSpy = jasmine.createSpyObj('NotificationService', ['loadNotifications', 'pingUnread'], {
      openState$: of(false), unreadCount$: of(0)
    });

    await TestBed.configureTestingModule({
      declarations: [NavBarComponent],
      imports: [HttpClientTestingModule, MatMenuModule],
      providers: [
        { provide: UserService, useValue: userSpy },
        { provide: AuthService, useValue: authSpy },
        { provide: ChatHostService, useValue: chatHostSpy },
        { provide: WsGatewayService, useValue: wsSpy },
        { provide: NotificationService, useValue: noteSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(NavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
