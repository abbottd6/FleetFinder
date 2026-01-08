import {Injectable, OnDestroy} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {BehaviorSubject, map, Observable, Subject} from "rxjs";
import {tap} from "rxjs/operators";
import {HttpClient} from "@angular/common/http";
import {ModIssueViewModel} from "../../../models/moderation/ModIssueViewModel";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";
import {ModListingActionViewModel} from "../../../models/moderation/ModListingActionViewModel";

interface GetResponse {
  _embedded: {
    groupListingResponseDtoes: GroupListingViewModel[];
  }
}

@Injectable({
  providedIn: 'root'
})
export class ModApiService implements OnDestroy {
  private destroy$ = new Subject<void>();
  private baseUrl = `${environment.apiBaseUrl}/modctrl`;
  private clearIssueUrl = `${environment.apiBaseUrl}/modctrl/mod_clear_issue`;
  private getIssuesUrl = `${environment.apiBaseUrl}/modctrl/get_issues_page`;
  private getActionsUrl = `${environment.apiBaseUrl}/modctrl/weeks_actions`;
  private modDeleteListingUrl = `${environment.apiBaseUrl}/modctrl/mod_delete_listing`;
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);

  constructor(private httpClient: HttpClient) {}

  //TODO change this to use a dto matching backend 'ManualModDeleteDto': Long groupId, Integer reportBasis, String modNote
  modDeleteListing(listingId: number, basis: number) {
    const requestBody = {
      groupId: listingId,
      reportBasis: basis
    }

    return this.httpClient.post<any>(`${this.modDeleteListingUrl}`, requestBody);
  }

  modClearIssue(issueId: number, note: string) {
    const requestBody = {
      issueId: issueId,
      note: note
    }

    return this.httpClient.put<any>(this.clearIssueUrl, requestBody);
  }

  modGetGroupListings(): Observable<GroupListingViewModel[]> {
    return this.httpClient.get<GetResponse>(this.baseUrl).pipe(
      tap(response => {
        if (!environment.production) {
          console.log('Raw API Response: ', response);
        }
      }),
      map(response => response._embedded.groupListingResponseDtoes),
      tap(modGroupListings => {
        if(!environment.production) {
          console.log('Transformed mod view group Listings: ', modGroupListings);
        }
      })
    )
  }

  modGetIssues(page: number, size: number, sortField: string, sortDirection: string): Observable<Page<ModIssueViewModel>> {
    const requestBody = {
      page,
      size,
      sortField: sortField,
      sortDirection: sortDirection,
    }

    return this.httpClient.post<Page<ModIssueViewModel>>(this.getIssuesUrl, requestBody).pipe(
      tap(response => {
        if (!environment.production) {
          console.log('Raw API Response: ', response);
        }
      })
    )
  }

  modGetWeeksActions(page: number, size: number): Observable<Page<ModListingActionViewModel>> {
    const requestBody = {
      pageIdx: page,
      pageSize: size
    }

    return this.httpClient.post<Page<ModListingActionViewModel>>(this.getActionsUrl, requestBody);
  }

  public refreshModPanel() {
    this.refreshTrigger$.next();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}


