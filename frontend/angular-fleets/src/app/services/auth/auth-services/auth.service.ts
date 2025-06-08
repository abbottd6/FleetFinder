import {inject, Injectable} from '@angular/core';
import {AuthenticatedResult, OidcSecurityService, PopupOptions} from "angular-auth-oidc-client";
import {
  BehaviorSubject,
  catchError,
  filter,
  firstValueFrom,
  map,
  merge,
  Observable, shareReplay, startWith,
  switchMap,
  take,
  tap,
  throwError, withLatestFrom
} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {User} from "../../../models/user/user";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly oidc = inject(OidcSecurityService);
  private readonly http = inject(HttpClient);

  userData$ = this.oidc.userData$;
  isLoggedIn$ = this.oidc.isAuthenticated$.pipe(map(r => r.isAuthenticated))
  configuration$ = this.oidc.getConfiguration();
  authResult$: Observable<AuthenticatedResult> = this.oidc.isAuthenticated$;


  // Reactive provisioning
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  private profile$ = this.userData$.pipe(
    filter(d => !!d && !!d.userData)
  );

  public localUser$: Observable<User> = this.refreshTrigger$.pipe(
    // fire once immediately, then whenever refreshTrigger$ .next()s
    startWith(undefined),
    // pair with latest profile
    withLatestFrom(this.profile$),
    // extract the profile
    switchMap(([, packageData]) =>
      this.http.get<Partial<User>>('/api/users/me').pipe(
        catchError(err => {
          if (err.status === 404) {
            return this.http.post<Partial<User>>('/api/users/create-user', {
              keycloakId: packageData.userData.sub,
              username: packageData.userData.preferred_username,
              email: packageData.userData.email,
            });
          }
          return throwError(() => err);
        }),
        map(raw => new User(
          raw.userId!,
          raw.username!,
          raw.email!,
          raw.server!,
          raw.org!,
          raw.about!,
          raw.acctCreated!,
          raw.groupListingsDto!
        ))
      )
    ),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  public localUsername$ = this.localUser$.pipe(map(u => u.username));

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
