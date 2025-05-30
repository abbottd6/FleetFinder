import {inject, Injectable} from '@angular/core';
import {AuthenticatedResult, OidcSecurityService, PopupOptions} from "angular-auth-oidc-client";
import {filter, map, Observable, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly oidcSecurityService = inject(OidcSecurityService);
  private readonly http = inject(HttpClient);

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
      .pipe(
        filter(({ isAuthenticated }) => isAuthenticated),
        tap(() => this.provisionLocalUser())
      )
      .subscribe();
  }

  private provisionLocalUser() {
    this.http.post('/api/users/me', null).subscribe({
      next: () => console.log("New user created"),
      error: err => console.error("User provisioning failed", err),
    });
  }

  login() {
    return this.oidcSecurityService.authorize();
  }

  loginWithPopup() {
    // calculate a centered position (optional)
    const popupWidth = 500;
    const popupHeight = 600;
    const left = Math.round((window.screen.width  - popupWidth)  / 2);
    const top  = Math.round((window.screen.height - popupHeight) / 3);

    const popupOptions: PopupOptions = {
      width:  popupWidth,
      height: popupHeight,
      left,
      top
    };

    return this.oidcSecurityService
      .authorizeWithPopUp({}, popupOptions)
      .pipe(
        tap(({ isAuthenticated}) => {
          if (isAuthenticated) {
            this.provisionLocalUser();
          }
      })
      )
      .subscribe();
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
      .pipe(
        tap(({ isAuthenticated }) => {
          if (isAuthenticated) {
            this.provisionLocalUser();
          }
        })
      )
      .subscribe();
  }
}
