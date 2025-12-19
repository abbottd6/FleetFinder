import {inject, Injectable} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, of, shareReplay, startWith, switchMap, tap} from "rxjs";
import {AddBookmarkRequest} from "../../../models/bookmark-requests/add-bookmark-request";
import {AuthService} from "../../auth/auth-services/auth.service";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../environments/environment";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";

@Injectable({
  providedIn: 'root'
})
export class BookmarkApiService {

  private bookmarksUrl = `${environment.apiBaseUrl}/users/my/bookmarks`;
  private bookmarksBriefUrl = `${environment.apiBaseUrl}/users/my/bookmarks_brief`;
  private deleteMultipleUrl = `${environment.apiBaseUrl}/users/my/bookmarks/delete_multiple`
  private auth = inject(AuthService);

  private refreshBookmarksSubject = new BehaviorSubject<void>(undefined);
  readonly refreshBookmarks$ = this.refreshBookmarksSubject.asObservable();
  readonly bookmarksBrief$: Observable<number[]> = combineLatest([
    this.auth.isLoggedIn$,
    this.refreshBookmarks$
  ]).pipe(
    switchMap(([isLoggedIn]) =>
      isLoggedIn ? this.getBookmarksBrief()
        : of<number[]>([])
    ),
    startWith([] as number[]),
    shareReplay(1)
  );

  constructor(private httpClient: HttpClient) {}

  getBookmarks(pageIdx: number, pageSize: number): Observable<Page<GroupListingViewModel>> {
    const requestBody = {
      pageIdx: pageIdx,
      pageSize: pageSize
    }
    return this.httpClient.post<Page<GroupListingViewModel>>(`${this.bookmarksUrl}_get`, requestBody);
  }

  getBookmarksBrief(): Observable<any> {
    return this.httpClient.get<any>(this.bookmarksBriefUrl).pipe(
      tap(response => console.log("bookmark brief response: ", response))
    );
  }

  addBookmark(request: AddBookmarkRequest): Observable<any> {
    return this.httpClient.post<any>(this.bookmarksUrl, request).pipe(
      tap(() => this.triggerBookmarksRefresh())
    );
  }

  deleteBookmark(request: number): Observable<any> {
    return this.httpClient.delete<any>(`${this.bookmarksUrl}/${request}`).pipe(
      tap(() => this.triggerBookmarksRefresh())
    )
  }

  deleteMultipleBookmarks(groupIds: number[]): Observable<any> {
    return this.httpClient.delete<any>(this.deleteMultipleUrl, {
      body: groupIds
    }).pipe(
      tap(() => this.triggerBookmarksRefresh())
    )
  }

  private triggerBookmarksRefresh(): void {
    this.refreshBookmarksSubject.next();
  }
}
