import { TestBed } from '@angular/core/testing';
import { of, NEVER } from 'rxjs';

import { WsGatewayService } from './ws-gateway.service';
import { AuthService } from '../auth/auth-services/auth.service';

describe('WsGatewayService', () => {
  let service: WsGatewayService;
  let authSpy: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authSpy = jasmine.createSpyObj('AuthService', ['login', 'logout'], {
      'isLoggedIn$': of(false),
      'tokenReady$': NEVER,
      'authClaims$': NEVER,
    });

    TestBed.configureTestingModule({
      providers: [
        WsGatewayService,
        { provide: AuthService, useValue: authSpy },
      ]
    });

    service = TestBed.inject(WsGatewayService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('isConnected$ starts as false before connect() is called', (done) => {
    service.isConnected$.subscribe(connected => {
      expect(connected).toBeFalse();
      done();
    });
  });

  it('isConnected() returns false synchronously before connect() is called', () => {
    expect(service.isConnected()).toBeFalse();
  });
});
