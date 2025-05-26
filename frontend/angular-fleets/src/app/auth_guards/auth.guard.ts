import {Injectable, Input} from "@angular/core";
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from "@angular/router";
import { Observable } from "rxjs";
import { map, tap } from "rxjs/operators";
import { OidcSecurityService } from "angular-auth-oidc-client";
import {AuthService} from "../services/auth.service";

@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {

  constructor(private auth: AuthService,
              private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> {
    return this.auth.isLoggedIn$.pipe(
      tap(isAuth => {
        if (!isAuth) {
          this.auth.loginWithPopup();
        }
      }),
      map(isAuth =>
        isAuth
          ? true
          : this.router.parseUrl('/login-failed')
      )
    );
  }
}


