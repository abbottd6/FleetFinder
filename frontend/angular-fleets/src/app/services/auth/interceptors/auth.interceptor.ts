import {Injectable} from "@angular/core";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {OidcSecurityService} from "angular-auth-oidc-client";
import {mergeMap, Observable, take} from "rxjs";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private oidc: OidcSecurityService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return this.oidc.getAccessToken().pipe(
      take(1),
      mergeMap(token => {
        const authReq = token
          ? req.clone({
            setHeaders: {Authorization: `Bearer ${token}`}
          })
          : req;
        return next.handle(authReq);
      })
    );
  }
}
