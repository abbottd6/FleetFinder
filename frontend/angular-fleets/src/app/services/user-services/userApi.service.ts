import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {PrivateUser} from "../../models/private-user/private-user";
import {catchError, map, Observable, throwError} from "rxjs";
import {UserDataResult} from "angular-auth-oidc-client";
import {UpdateUserRequest} from "../../models/private-user/update-user-request";

@Injectable({
  providedIn: 'root'
})

export class UserApiService {

  constructor(private http: HttpClient) {}

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
          raw.email!,
          raw.server!,
          raw.org!,
          raw.about!,
          raw.acctCreated!,
          raw.lastAccess!,
          raw.discordUsername!,
          raw.externalSysNotesEnabled!,
          raw.externalGroupNotesEnabled!,
          raw.externalSocialNotesEnabled!,
          raw.groupListingsDto!
        );
      })
    );
  }

  public updateMe(userData: UpdateUserRequest) {
    return this.http.put('/api/users/update_me', userData);
  }

  public discordMe() {
    return this.http.get<{ url: string }>('/api/users/discord_me').pipe(
      map(response => response.url)
    );
  }

  public removeDiscord() {
    return this.http.delete('/api/users/remove_discord');
  }

  public deleteUser() {
    return this.http.delete<Partial<PrivateUser>>('/api/users/delete_me');
  }
}
