import {inject, Injectable, OnChanges, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {PublicUser} from "../../models/public-user/public-user";
import {
  BehaviorSubject,
  catchError, combineLatest,
  filter,
  map,
  Observable,
  shareReplay, Subject,
  switchMap, takeUntil,
  throwError,
  withLatestFrom
} from "rxjs";
import {environment} from '../../../environments/environment';
import {PrivateUser} from "../../models/private-user/private-user";
import {AuthService} from "../auth/auth-services/auth.service";
import {UserApiService} from "./userApi.service";

@Injectable({
  providedIn: 'root'
})

export class UserService implements OnDestroy {
  private destroy$ = new Subject<void>();
  public role!: string;
  private readonly http = inject(HttpClient);
  private auth = inject(AuthService);
  private api = inject(UserApiService);
  private baseUrl = `${environment.apiBaseUrl}/users`;

  constructor() {
    combineLatest([this.auth.isLoggedIn$, this.auth.authClaims$])
      .pipe(
        shareReplay(1),
        takeUntil(this.destroy$),
        filter(([loggedIn, claims]) => loggedIn && !!claims && !! claims.userData),
      )
      .subscribe()
  }

  getUserById(userId: number): Observable<PublicUser> {
    const url = `${this.baseUrl}/${userId}`;
    return this.http.get<PublicUser>(url);
  }

  // Reactive provisioning
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  private profile$ = this.auth.authClaims$.pipe(
    filter(data => !!data && !!data.userData)
  );

  getRole(): string {
    this.profile$.pipe(
      map(data => data.userData.roles as string[]))
      .subscribe((roles: string[]) => {

        for (const role of roles) {
          if (role == 'admin') {
            this.role = 'admin';
            break;
          } else if (role == 'mod') {
            this.role = 'mod'
            break
          } else {
            this.role = 'user'
          }
        }
      });
    return this.role;
  }

  // local version of keycloak's user
  public localUser$: Observable<PrivateUser> = this.refreshTrigger$.pipe(
    // pair with latest profile
    withLatestFrom(this.profile$),
    // extract the profile
    switchMap(([, profile]) =>
      this.api.getMe(profile)
    ),
    shareReplay({bufferSize: 1, refCount: true})
  );

  public localUsername$ = this.localUser$.pipe(map(userObj => userObj.username));

  public refreshUser() {
    this.refreshTrigger$.next();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
