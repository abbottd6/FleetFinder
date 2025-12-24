import {inject, Injectable} from '@angular/core';
import {OidcSecurityService} from "angular-auth-oidc-client";
import {map, take} from "rxjs";
import {Router} from "@angular/router";

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
}
