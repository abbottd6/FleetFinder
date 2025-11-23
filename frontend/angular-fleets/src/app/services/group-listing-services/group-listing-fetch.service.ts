import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, tap} from "rxjs";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {environment} from '../../../environments/environment';
import {ListingFilterRequest} from "../../models/listing-filter/listing-filter-request";

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class GroupListingFetchService {

  private baseUrl = `${environment.apiBaseUrl}/group-listings`;

  constructor(private httpClient: HttpClient) { }

  searchGroupListings(
    filters: ListingFilterRequest,
    page: number,
    size: number
  ): Observable<Page<GroupListingViewModel>> {
    const requestBody = {
      ...filters,
      page,
      size};

    console.log(requestBody);
    return this.httpClient.post<Page<GroupListingViewModel>>(`${this.baseUrl}/search`, requestBody)
      .pipe(
        tap(response => {
          if (!environment.production) {
            console.log('Raw API Response: ', response);
          }
        })
      );
  }
}

interface GetResponse {
  _embedded: {
    groupListingResponseDtoes: GroupListingViewModel[];
  }
}

