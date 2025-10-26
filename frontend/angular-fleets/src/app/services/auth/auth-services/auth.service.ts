import {inject, Injectable} from '@angular/core';
import {AuthenticatedResult, OidcSecurityService, PopupOptions} from "angular-auth-oidc-client";
import {
  BehaviorSubject,
  catchError,
  filter,
  map,
  merge,
  Observable, shareReplay,
  switchMap,
  tap,
  throwError, withLatestFrom
} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {PublicUser} from "../../../models/public-user/public-user";
import {PrivateUser} from "../../../models/private-user/private-user";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly oidc = inject(OidcSecurityService);


  // Raw profile/claims OIDC Observable
  // read only
  // use for username, email, roles straight from kc
  authClaims$ = this.oidc.userData$;

  // isAuthenticated is an object with a boolean for authState and userData<any>
  // extract just the authState for isLoggedIn$ boolean
  isLoggedIn$ = this.oidc.isAuthenticated$
    .pipe(map(oidcAuthObj => oidcAuthObj.isAuthenticated))

  // OIDC client metadata/settings (auth URL, clientID, redirect URIs, scopes, etc.)
  configuration$ = this.oidc.getConfiguration();

  constructor() {
    this.oidc
      .checkAuth()
      .pipe(
        filter(({ isAuthenticated }) => isAuthenticated),
        tap(() => this.refreshUser())
      )
      .subscribe();
  }

  logout() {
    return this.oidc
      .logoff()
      .subscribe((result) => console.log(result));
  }

  loginWithPopup() {
    // calculate a centered position
    const popupWidth = 550;
    const popupHeight = 600;
    const left = Math.round((window.screen.width  - popupWidth)  / 2);
    const top  = Math.round((window.screen.height - popupHeight) / 3);

    const popupOptions: PopupOptions = {
      width:  popupWidth,
      height: popupHeight,
      left,
      top
    };

    return this.oidc
      .authorizeWithPopUp({}, popupOptions)
      .pipe(
        tap(({ isAuthenticated}) => {
          if (isAuthenticated) {
            this.refreshUser();
          }
      })
      )
      .subscribe();
  }

  registerWithPopup() {
    // calculate a centered position
    const popupWidth = 550;
    const popupHeight = 600;
    const left = Math.round((window.screen.width  - popupWidth)  / 2);
    const top  = Math.round((window.screen.height - popupHeight) / 3);

    const popupOptions: PopupOptions = {
      width:  popupWidth,
      height: popupHeight,
      left,
      top
    };

    return this.oidc
      .authorizeWithPopUp({customParams: {screen_hint: 'signup'}}, popupOptions)
      .pipe(
        tap(({ isAuthenticated }) => {
          if (isAuthenticated) {
            this.refreshUser();
          }
        })
      )
      .subscribe();
  }
}
