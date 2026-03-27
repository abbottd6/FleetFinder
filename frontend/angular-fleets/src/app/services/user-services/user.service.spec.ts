import { TestBed } from '@angular/core/testing';
import { of, NEVER } from 'rxjs';

import { UserService } from './user.service';
import { AuthService } from '../auth/auth-services/auth.service';
import { UserApiService } from './userApi.service';
import { WsGatewayService } from '../websocket-messaging/ws-gateway.service';

describe('UserService', () => {
  let service: UserService;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', [], {
      isLoggedIn$: of(false),
      authClaims$: of(null),
      tokenReady$: NEVER,
      accessToken$: NEVER
    });

    const userApiSpy = jasmine.createSpyObj('UserApiService', ['getMe']);
    userApiSpy.getMe.and.returnValue(NEVER);

    const wsSpy = jasmine.createSpyObj('WsGatewayService', ['connect', 'disconnect', 'isConnected'], {
      isConnected$: of(false),
      chatMessage$: NEVER,
      chatConversation$: NEVER,
      notifications$: of([]),
      totalUnread$: of(0),
      perConvUnread$: of({}),
      notificationUnread$: of(0)
    });

    TestBed.configureTestingModule({
      providers: [
        UserService,
        { provide: AuthService, useValue: authSpy },
        { provide: UserApiService, useValue: userApiSpy },
        { provide: WsGatewayService, useValue: wsSpy }
      ]
    });

    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('sessionUser$ emits null initially when not logged in', (done) => {
    service.sessionUser$.subscribe(user => {
      expect(user).toBeNull();
      done();
    });
  });
});
