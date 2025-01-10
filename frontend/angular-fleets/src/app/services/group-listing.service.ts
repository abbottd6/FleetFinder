import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable, tap} from "rxjs";
import {GroupListing} from "../common/group-listing";

@Injectable({
  providedIn: 'root'
})
export class GroupListingService {

  private baseUrl = '/api/groupListings';

  constructor(private httpClient: HttpClient) { }

  getGroupListings(): Observable<GroupListing[]> {
    return this.httpClient.get<GetResponse>(this.baseUrl).pipe(
      tap(response => console.log('Raw API Response: ', response)),
      map(response => response._embedded.groupListingDtoes),
      tap(groupListings => console.log('Transformed data: ', groupListings)),
    )
  }
}

interface GetResponse {
  _embedded: {
    groupListingDtoes: GroupListing[];
  }
}

