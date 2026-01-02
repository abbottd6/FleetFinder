import {DestroyRef, inject, Injectable} from '@angular/core';
import {
  BehaviorSubject,
  combineLatest, distinctUntilChanged,
  filter,
  map,
  Observable, of,
  shareReplay,
  switchMap,
  withLatestFrom
} from "rxjs";
import {PrivateUser} from "../../models/private-user/private-user";
import {AuthService} from "../auth/auth-services/auth.service";
import {UserApiService} from "./userApi.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

export enum UserRole {
  admin = 'admin',
  mod = 'mod',
  user = 'user'
}

export interface SessionUser {
  userId: number,
  username: string,
  email: string,
  server: string,
  org: string,
  about: string,
  acctCreated: Date,
  lastAccess: Date,
  primaryRole: UserRole,
  groupListingsDto: GroupListingViewModel[]
}

@Injectable({
  providedIn: 'root'
})

export class UserService {
  private destroyRef = inject(DestroyRef)
  protected auth = inject(AuthService);
  private userApi = inject(UserApiService);

  private readonly userSubject = new BehaviorSubject<SessionUser | null>(null);
  readonly sessionUser$ = this.userSubject.asObservable();
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);

  private kcProfile$ = this.auth.authClaims$.pipe(
    filter(data => !!data && !!data.userData)
  );

  // PRIVATE USER
  public ffPrivateUser$: Observable<PrivateUser> = combineLatest([
    this.refreshTrigger$,
    this.kcProfile$
  ]).pipe(
    takeUntilDestroyed(this.destroyRef),
    switchMap(([, profile]) =>
      this.userApi.getMe(profile)
    ),
    shareReplay({bufferSize: 1, refCount: true})
  );

  constructor() {
    this.auth.isLoggedIn$.pipe(
      takeUntilDestroyed(this.destroyRef),

      switchMap(loggedIn => {
        if(!loggedIn) return of<SessionUser | null>(null);

        return combineLatest([this.kcProfile$, this.ffPrivateUser$]).pipe(
          filter(([, ffPrivate]) => !!ffPrivate),
          map(([kcClaims, ffPrivate]) => {
            const primaryRole = this.extractRole(kcClaims.userData.roles) ?? UserRole.user;
            const email = kcClaims.userData.email;
            return {
              ...ffPrivate,
              primaryRole,
              email,
            } satisfies SessionUser;
          }),
          distinctUntilChanged((a, b) =>
            a?.userId === b?.userId &&
            a?.primaryRole === b?.primaryRole &&
            a?.groupListingsDto === b?.groupListingsDto &&
            a?.lastAccess === b?.lastAccess
          )
        );
      })
    ).subscribe(user => this.userSubject.next(user));


    this.auth.isLoggedIn$.pipe(takeUntilDestroyed(this.destroyRef))
      .pipe(filter(value => !value))
      .subscribe(() => this.userSubject.next(null));
  }

  extractRole(roles: string[]) {
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

  get sessionUser(): SessionUser | null { return this.userSubject.value ?? null; }
  get userId(): number | null { return this.userSubject.value?.userId ?? null; }
  get username(): string | null { return this.userSubject.value?.username ?? null; }
  get server(): string | null { return this.userSubject.value?.server ?? null; }
  get org(): string | null { return this.userSubject.value?.org ?? null; }
  get about(): string | null { return this.userSubject.value?.about ?? null; }
  get acctCreated(): Date | null { return this.userSubject.value?.acctCreated ?? null; }
  get lastAccess(): Date | null { return this.userSubject.value?.lastAccess ?? null; }
  get primaryRole(): UserRole | null { return this.userSubject.value?.primaryRole ?? null; }
  get userListings(): GroupListingViewModel[] { return this.userSubject.value?.groupListingsDto ?? []}

  public refreshUser() { this.refreshTrigger$.next(); }
}
