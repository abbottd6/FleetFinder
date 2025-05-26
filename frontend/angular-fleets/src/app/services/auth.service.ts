import {inject, Injectable} from '@angular/core';
import {AuthenticatedResult, OidcSecurityService} from "angular-auth-oidc-client";
import {map, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly oidcSecurityService = inject(OidcSecurityService);

  userData$ = this.oidcSecurityService.userData$;
  username$ = this.oidcSecurityService.userData$.pipe(
    map(res =>
      res.userData?.preferred_username
      || res.userData?.given_name
      || 'User'
    )
  );

  configuration$ = this.oidcSecurityService.getConfiguration();
  authResult$: Observable<AuthenticatedResult> = this.oidcSecurityService.isAuthenticated$;

  isLoggedIn$: Observable<boolean> =
    this.authResult$.pipe(map(r => r.isAuthenticated))

  constructor() {
    this.oidcSecurityService
      .checkAuth()
      .subscribe(({
                    isAuthenticated, userData, accessToken,
                    errorMessage
                  }) => {
        console.log(isAuthenticated);
        console.log(userData);
        console.log(accessToken);
        console.log(errorMessage);
      });
  }

  login() {
    return this.oidcSecurityService.authorize();
  }

  loginWithPopup() {
    return this.oidcSecurityService
      .authorizeWithPopUp()
      .subscribe(({ isAuthenticated, userData, accessToken, errorMessage }) => {
        console.log(isAuthenticated);
        console.log(userData);
        console.log(accessToken);
        console.log(errorMessage);
      })
  }

  openWindow() {
    window.open('/', '_blank');
  }

  forceRefreshSession() {
    return this.oidcSecurityService
      .forceRefreshSession()
      .subscribe((result) => console.warn(result));
  }

  logout() {
    return this.oidcSecurityService
      .logoff()
      .subscribe((result) => console.log(result));
  }

  registerWithPopup() {
    return this.oidcSecurityService
      .authorizeWithPopUp({customParams: {screen_hint: 'signup'}})
      .subscribe(({ isAuthenticated, userData, accessToken, errorMessage }) => {
        console.log(isAuthenticated);
        console.log(userData);
        console.log(accessToken);
        console.log(errorMessage);
      })
  }
}
