import {inject, Injectable, OnDestroy} from '@angular/core';
import {
  BehaviorSubject,
  combineLatest, distinctUntilChanged,
  filter,
  map,
  Observable, of,
  shareReplay, Subject,
  switchMap, takeUntil,
  withLatestFrom
} from "rxjs";
import {PrivateUser} from "../../models/private-user/private-user";
import {AuthService} from "../auth/auth-services/auth.service";
import {UserApiService} from "./userApi.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {PublicUser} from "../../models/public-user/public-user";

export enum UserRole {
  admin = 'admin',
  mod = 'mod',
  user = 'user'
}

@Injectable({
  providedIn: 'root'
})

export class UserService implements OnDestroy {
  private destroy$ = new Subject<void>();
  protected auth = inject(AuthService);
  private userApi = inject(UserApiService);

  private readonly userSubject = new BehaviorSubject<PublicUser | null>(null);
  readonly sessionUser$ = this.userSubject.asObservable();
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);

  private kcProfile$ = this.auth.authClaims$.pipe(
    filter(data => !!data && !!data.userData)
  );

  // PRIVATE USER
  public localUser$: Observable<PrivateUser> = this.refreshTrigger$.pipe(
    withLatestFrom(this.kcProfile$),
    switchMap(([, profile]) =>
      this.userApi.getMe(profile)
    ),
    shareReplay({bufferSize: 1, refCount: true})
  );

  constructor() {
    this.auth.isLoggedIn$
      .pipe(
        distinctUntilChanged(),
        switchMap(loggedIn => {
          if(!loggedIn) return of<PublicUser | null>(null);

          return combineLatest([this.auth.authClaims$, this.kcProfile$]).pipe(
              filter(([claims, profile]) => !!claims && !!profile),
                switchMap(([claims, profile]) =>
                  this.userApi.getMe(profile).pipe(
                    map(privateProfile => {
                      const role = this.defineRole(claims.userData.roles) ?? UserRole.user;
                      return {
                        ...privateProfile,
                        role,
                      } satisfies PublicUser;
                    })
                  )
                )
              )
          }),
          distinctUntilChanged((a, b) =>
              a?.userId === b?.userId &&
              a?.role === b?.role),
      )
      .subscribe(user => this.userSubject.next(user));

    this.auth.isLoggedIn$
      .pipe(filter(value => !value))
      .subscribe(() => this.userSubject.next(null));
  }

  defineRole(roles: string[]) {
    let role: UserRole = UserRole.user;

    if (!roles) return UserRole.user;
    if (roles.includes('admin')) {
      role = UserRole.admin;
    } else if (roles.includes('mod')) {
      role = UserRole.mod;
    } else {
      role = UserRole.user
    }
    return role;
  }

  get userId(): number | null { return this.userSubject.value?.userId ?? null; }
  get username(): string | null { return this.userSubject.value?.username ?? null; }
  get role(): UserRole | null { return this.userSubject.value?.role ?? null; }



  public userListings$: Observable<GroupListingViewModel[]> = this.localUser$.pipe(takeUntil(this.destroy$)).pipe(
    map(user => user.groupListingsDto ?? [])
  )

  public refreshUser() { this.refreshTrigger$.next(); }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
