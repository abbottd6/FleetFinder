import {inject, Injectable} from "@angular/core";
import {
  BehaviorSubject,
  catchError,
  filter,
  map,
  Observable,
  shareReplay,
  switchMap,
  throwError,
  withLatestFrom
} from "rxjs";
import {PrivateUser} from "../../models/private-user/private-user";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})

export class UserApiService {

  private readonly http = inject(HttpClient);

  // Reactive provisioning
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  private profile$ = this.userData$.pipe(
    filter(d => !!d && !!d.userData)
  );


  // local version of keycloak's user
  public localUser$: Observable<PrivateUser> = this.refreshTrigger$.pipe(
    // pair with latest profile
    withLatestFrom(this.profile$),
    // extract the profile
    switchMap(([, profile]) =>
      this.http.get<Partial<PrivateUser>>('/api/users/me').pipe(
        catchError(err => {
          if (err.status === 404) {
            return this.http.post<Partial<PrivateUser>>('/api/users/create-user', {
              keycloakId: profile.userData.sub,
              username: profile.userData.preferred_username,
              email: profile.userData.email,
            });
          }
          return throwError(() => err);
        }),
        map(raw => {
          const roles: string[] =
            profile.userData.realm_access?.roles || [];

          return new PrivateUser(
            raw.userId!,
            raw.username!,
            raw.email!,
            raw.server!,
            raw.org!,
            raw.about!,
            raw.acctCreated!,
            raw.groupListingsDto!,
            roles
          )
        })
      )
    ),
    shareReplay({bufferSize: 1, refCount: true})
  );

  public localUsername$ = this.localUser$.pipe(map(userObj => userObj.username));

  public refreshUser() {
    this.refreshTrigger$.next(undefined);
  }
}
