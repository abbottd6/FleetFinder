import {inject, Injectable} from '@angular/core';
import {OidcSecurityService, PopupOptions} from "angular-auth-oidc-client";
import {map} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly oidc = inject(OidcSecurityService);

  // Raw profile/claims OIDC Observable
  // read only
  // use for username, email, roles straight from kc

  public authClaims$ = this.oidc.userData$;

  // isAuthenticated is an object with a boolean for authState and userData<any>
  // extract just the authState for isLoggedIn$ boolean
  public isLoggedIn$ = this.oidc.isAuthenticated$
    .pipe(map(oidcAuthObj => oidcAuthObj.isAuthenticated))

  // OIDC client metadata/settings (auth URL, clientID, redirect URIs, scopes, etc.)
  configuration$ = this.oidc.getConfiguration();

  constructor() {
    this.oidc
      .checkAuth().subscribe();
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
      .authorizeWithPopUp({}, popupOptions).subscribe();
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
      .authorizeWithPopUp({customParams: {screen_hint: 'signup'}}, popupOptions).subscribe();
  }
}
