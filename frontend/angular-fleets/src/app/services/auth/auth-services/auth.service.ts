import {inject, Injectable} from '@angular/core';
import {OidcSecurityService} from "angular-auth-oidc-client";
import {map, take, tap} from "rxjs";
import {Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly oidc = inject(OidcSecurityService);

  public authClaims$ = this.oidc.userData$;
  private authenticated!: boolean;

  public isLoggedIn$ = this.oidc.isAuthenticated$
    .pipe(map(oidcAuthObj => oidcAuthObj.isAuthenticated))

  // OIDC client metadata/settings (auth URL, clientID, redirect URIs, scopes, etc.)
  // configuration$ = this.oidc.getConfiguration();

  constructor(private router: Router, private snackBar: MatSnackBar) {
    this.oidc.checkAuth().pipe(take(1)).subscribe(({ isAuthenticated }) => {
      if (isAuthenticated) {
        const url = sessionStorage.getItem('post_login_url') ?? '/';
        sessionStorage.removeItem('post_login_url');
        this.router.navigateByUrl(url);
      }
    });
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
