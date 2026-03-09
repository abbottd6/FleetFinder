import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { of, NEVER } from 'rxjs';

import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let oidcSpy: jasmine.SpyObj<OidcSecurityService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    oidcSpy = jasmine.createSpyObj('OidcSecurityService', ['checkAuth', 'getAccessToken', 'logoff', 'authorize'], {
      'userData$': of({ userData: null }),
      'isAuthenticated$': of({ isAuthenticated: false }),
    });
    oidcSpy.checkAuth.and.returnValue(of({ isAuthenticated: false, userData: null, accessToken: '', idToken: '', configId: '' }));
    oidcSpy.getAccessToken.and.returnValue(NEVER);
    oidcSpy.logoff.and.returnValue(of(null));

    routerSpy = jasmine.createSpyObj('Router', ['navigateByUrl']);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        { provide: OidcSecurityService, useValue: oidcSpy },
        { provide: Router, useValue: routerSpy },
      ]
    });

    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('isLoggedIn$ emits a boolean', (done) => {
    service.isLoggedIn$.subscribe(val => {
      expect(typeof val).toBe('boolean');
      done();
    });
  });

  it('logout() calls oidc.logoff()', () => {
    service.logout();
    expect(oidcSpy.logoff).toHaveBeenCalled();
  });
});
