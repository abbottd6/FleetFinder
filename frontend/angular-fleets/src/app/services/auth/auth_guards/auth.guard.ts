import {inject, Injectable} from "@angular/core";
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from "@angular/router";
import {filter, from, Observable, of, switchMap, take} from "rxjs";
import { map, tap } from "rxjs/operators";
import {AuthService} from "../auth-services/auth.service";
import {OidcSecurityService} from "angular-auth-oidc-client";

@Injectable({providedIn: 'root'})
export class AuthGuard implements CanActivate {

  constructor(private auth: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> {

    return this.auth.isLoggedIn$.pipe(
      take(1),
      tap(isAuth => {
        if (!isAuth) {
          sessionStorage.setItem('post_login_url', state.url);

          this.auth.login();
        }
      }),
      map(isAuth => isAuth)

        // return this.router.createUrlTree(['/login'], {
        //   queryParams: { returnUrl: state.url }
        // });

    );
  }
}


