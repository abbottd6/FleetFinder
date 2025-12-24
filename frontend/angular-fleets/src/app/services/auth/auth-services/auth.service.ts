import {inject, Injectable} from '@angular/core';
import {OidcSecurityService, PopupOptions} from "angular-auth-oidc-client";
import {BehaviorSubject, catchError, finalize, map, Observable, of, Subject, switchMap, take, tap} from "rxjs";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly oidc = inject(OidcSecurityService);

  private loginInFlight = false;

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

  constructor(private router: Router) {
    this.oidc.checkAuth().pipe(take(1)).subscribe(({ isAuthenticated }) => {
      if (isAuthenticated) {
        const url = sessionStorage.getItem('post_login_url') ?? '/';
        sessionStorage.removeItem('post_login_url');
        this.router.navigateByUrl(url);
      }
    });
  }

  logout() {
    return this.oidc
      .logoff()
      .subscribe((result) => console.log(result));
  }

  login() {
    return this.oidc.authorize();
  }

  register() {
    return this.oidc.authorize(undefined, {
      customParams: {
        prompt: 'create'
      }
    })
  }
  //
  // loginWithPopup$(): Observable<boolean> {
  //   if(this.loginInFlight) return of(false);
  //   this.loginInFlight = true;
  //
  //   // calculate a centered position for popup
  //   const popupWidth = 550;
  //   const popupHeight = 600;
  //   const left = Math.round((window.screen.width  - popupWidth)  / 2);
  //   const top  = Math.round((window.screen.height - popupHeight) / 3);
  //
  //   const popupOptions: PopupOptions = {
  //     width:  popupWidth,
  //     height: popupHeight,
  //     left,
  //     top
  //   };
  //
  //   // subscribe to authorization with the guard call stack
  //   return this.oidc.authorizeWithPopUp({}, popupOptions).pipe(
  //     take(1),
  //     //recheck auth after popup closes
  //     switchMap(() => this.oidc.checkAuth().pipe(take(1))),
  //     switchMap(() => this.isLoggedIn$.pipe(take(1))),
  //
  //
  //     catchError(() => {
  //       return of(false);
  //     }),
  //
  //     finalize(() => this.loginInFlight = false)
  //   )
  // }
  //
  // registerWithPopup() {
  //   // calculate a centered position
  //   const popupWidth = 550;
  //   const popupHeight = 600;
  //   const left = Math.round((window.screen.width  - popupWidth)  / 2);
  //   const top  = Math.round((window.screen.height - popupHeight) / 3);
  //
  //   const popupOptions: PopupOptions = {
  //     width:  popupWidth,
  //     height: popupHeight,
  //     left,
  //     top
  //   };
  //
  //   return this.oidc
  //     .authorizeWithPopUp({customParams: {screen_hint: 'signup'}}, popupOptions).subscribe();
  // }
}
