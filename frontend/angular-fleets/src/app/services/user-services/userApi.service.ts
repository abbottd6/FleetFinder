import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {PrivateUser} from "../../models/private-user/private-user";
import {catchError, map, Observable, throwError} from "rxjs";
import {AuthService} from "../auth/auth-services/auth.service";
import {UserDataResult} from "angular-auth-oidc-client";

@Injectable({
  providedIn: 'root'
})

export class UserApiService {

  constructor(private http: HttpClient, private readonly auth: AuthService) {}

  public getMe(profile: UserDataResult): Observable<PrivateUser> {
    return this.http.get<Partial<PrivateUser>>('/api/users/me').pipe(
      catchError(err => {
        if (err.status === 404) {
          return this.http.post<Partial<PrivateUser>>('/api/users/create-user', {
            keycloakId: profile.userData.sub,
            username: profile.userData.preferred_username,
          });
        }
        return throwError(() => err);
      }),
      map(raw => {
        const roles: string[] = profile.userData.realm_access?.roles || [];

        return new PrivateUser(
          raw.userId!,
          raw.username!,
          raw.server!,
          raw.org!,
          raw.about!,
          raw.acctCreated!,
          raw.lastAccess!,
          raw.groupListingsDto!
        );
      })
    );
  }
}
