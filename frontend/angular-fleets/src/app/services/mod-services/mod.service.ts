import {Injectable, OnDestroy} from '@angular/core';
import {environment} from "../../../environments/environment";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {BehaviorSubject, map, Observable, Subject} from "rxjs";
import {tap} from "rxjs/operators";
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class ModService implements OnDestroy {
  private destroy$ = new Subject<void>();
  private baseUrl = `${environment.apiBaseUrl}/modctrl`;
  private modDeleteListingUrl = `${environment.apiBaseUrl}/modctrl/mod_delete_listing`;
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);

  constructor(private httpClient: HttpClient) {}

  //TODO change this to use a dto matching backend 'ManualModDeleteDto': Long groupId, Integer reportBasis, String modNote
  modDeleteListing(listingId: number) {
    return this.httpClient.delete<any>(`${this.modDeleteListingUrl}/${listingId}`);
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
  public refreshModPanel() {
    this.refreshTrigger$.next();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

interface GetResponse {
  _embedded: {
    groupListingResponseDtoes: GroupListingViewModel[];
  }
}
