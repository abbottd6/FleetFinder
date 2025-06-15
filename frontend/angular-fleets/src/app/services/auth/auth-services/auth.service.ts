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
  private readonly http = inject(HttpClient);

  // Raw profile/claims OIDC Observable
  // read only
  // use for username, email, roles straight from kc
  userData$ = this.oidc.userData$;

  // isAuthenticated is an object with a boolean for authState and userData<any>
  // extract just the authState for isLoggedIn$ boolean
  isLoggedIn$ = this.oidc.isAuthenticated$
    .pipe(map(oidcAuthObj => oidcAuthObj.isAuthenticated))

  // OIDC client metadata/settings (auth URL, clientID, redirect URIs, scopes, etc.)
  configuration$ = this.oidc.getConfiguration();


  // Reactive provisioning
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  private profile$ = this.userData$.pipe(
    filter(d => !!d && !!d.userData)
  );

  // local version of keycloak's user
  public localUser$: Observable<PrivateUser> = this.refreshTrigger$.pipe(
    // pair with latest profile
    withLatestFrom(this.profile$),
    // extract the profile
    switchMap(([, profile]) =>
      this.http.get<Partial<PrivateUser>>('/api/users/me').pipe(
        catchError(err => {
          if (err.status === 404) {
            return this.http.post<Partial<PrivateUser>>('/api/users/create-user', {
              keycloakId: profile.userData.sub,
              username: profile.userData.preferred_username,
              email: profile.userData.email,
            });
          }
          return throwError(() => err);
        }),
        map(raw => {
          const roles: string[] =
            profile.userData.realm_access?.roles || [];

          return new PrivateUser(
            raw.userId!,
            raw.username!,
            raw.email!,
            raw.server!,
            raw.org!,
            raw.about!,
            raw.acctCreated!,
            raw.groupListingsDto!,
            roles
          )
        })
      )
    ),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  public localUsername$ = this.localUser$.pipe(map(userObj => userObj.username));

  constructor() {
    this.oidc
      .checkAuth()
      .pipe(
        filter(({ isAuthenticated }) => isAuthenticated),
        tap(() => this.refreshUser())
      )
      .subscribe();
  }

  public refreshUser() {
    this.refreshTrigger$.next(undefined);
  }

  login() {
    return this.oidc.authorize();
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

  openWindow() {
    window.open('/', '_blank');
  }

  forceRefreshSession() {
    return this.oidc
      .forceRefreshSession()
      .subscribe((result) => console.warn(result));
  }
}
