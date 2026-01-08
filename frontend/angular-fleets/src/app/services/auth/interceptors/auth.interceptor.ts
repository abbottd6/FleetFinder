import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {combineLatest, distinctUntilChanged, filter, map, mergeMap, Observable, take} from "rxjs";
import {OidcSecurityService} from "angular-auth-oidc-client";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private oidc: OidcSecurityService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const isAuthenticated$ = this.oidc.isAuthenticated$.pipe(
      map(authResponse => authResponse.isAuthenticated),
      distinctUntilChanged()
    );

    const token$ = this.oidc.getAccessToken().pipe(distinctUntilChanged());

    return combineLatest([isAuthenticated$, token$]).pipe(
      filter(([loggedIn, token]) => !loggedIn || !!token),
      take(1),
      mergeMap(([loggedIn, token]) => {
        const authReq = (loggedIn && token)
          ? req.clone({setHeaders: {Authorization: `Bearer ${token}`} })
          : req;
        return next.handle(authReq);
      })
    );
  }
}
