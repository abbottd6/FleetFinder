import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable, tap} from "rxjs";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {environment} from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class GroupListingFetchService {

  private baseUrl = `${environment.apiBaseUrl}/api/group-listings`;

  constructor(private httpClient: HttpClient) { }

  getGroupListings(): Observable<GroupListingViewModel[]> {
    return this.httpClient.get<GetResponse>(this.baseUrl).pipe(
      tap(response => console.log('Raw API Response: ', response)),
      map(response => response._embedded.groupListingResponseDtoes),
      tap(groupListings => console.log('Transformed data: ', groupListings)),
    )
  }
}

interface GetResponse {
  _embedded: {
    groupListingResponseDtoes: GroupListingViewModel[];
  }
}

