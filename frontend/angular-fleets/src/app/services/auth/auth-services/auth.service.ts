import {DestroyRef, inject, Injectable} from '@angular/core';
import {OidcSecurityService} from "angular-auth-oidc-client";
import {
  distinctUntilChanged,
  EMPTY,
  filter,
  map,
  Observable,
  shareReplay,
  switchMap,
  take,
  timer
} from "rxjs";
import {Router} from "@angular/router";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private destroyRef = inject(DestroyRef);

  private readonly oidc = inject(OidcSecurityService);

  public authClaims$ = this.oidc.userData$;

  public isLoggedIn$ = this.oidc.isAuthenticated$.pipe(
    takeUntilDestroyed(this.destroyRef),
    map(oidcAuthObj => oidcAuthObj.isAuthenticated),
    distinctUntilChanged(),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  public readonly accessToken$ = this.oidc.getAccessToken().pipe(
    takeUntilDestroyed(this.destroyRef),
    filter((token): token is string => !!token),
    distinctUntilChanged(),
    shareReplay({ bufferSize: 1, refCount: true })
  )


  public readonly tokenReady$: Observable<string> = this.isLoggedIn$.pipe(
    takeUntilDestroyed(this.destroyRef),
    switchMap(loggedIn => {
      if (!loggedIn) return EMPTY;

      // poll getAccessToken until it becomes non-empty
      return timer(0, 100).pipe(
        switchMap(() => this.oidc.getAccessToken().pipe(take(1))),
        filter(token => !!token),
        take(1),
      );
    }),
    distinctUntilChanged(),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  constructor(private router: Router) {
    this.oidc.checkAuth().pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(({ isAuthenticated }) => {
      if (isAuthenticated) {
        const url = sessionStorage.getItem('post_login_url');
        if(url) {
          sessionStorage.removeItem('post_login_url');
          this.router.navigateByUrl(url);
        }
      }
    });

    // this.oidc.isAuthenticated$.pipe(
    //   takeUntilDestroyed(this.destroyRef),
    //   map(({ isAuthenticated }) => isAuthenticated),
    //   distinctUntilChanged(),
    //   filter(authenticated => !authenticated)
    // ).subscribe(() => this.router.navigateByUrl('/'));
  }

  forceNewToken() {
    const user = this.oidc.forceRefreshSession();
    this.authClaims$ = this.oidc.getUserData();
    return user;
  }

  logout() {
    sessionStorage.setItem('post_logout_msg', 'true');

    return this.oidc.logoff().pipe(
      map(() => void 0)
    );
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
}
