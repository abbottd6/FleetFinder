import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {GroupListing} from "../common/group-listing";

@Injectable({
  providedIn: 'root'
})
export class GroupListingService {

  private baseUrl = 'http://localhost:8080/api/groupListings';

  constructor(private httpClient: HttpClient) { }

  getGroupListings(): Observable<GroupListing[]> {
    return this.httpClient.get<GetResponse>(this.baseUrl).pipe(
      map(response => response._embedded.groupListings),
    )
  }
}

interface GetResponse {
  _embedded: {
    groupListings: GroupListing[];
  }
}

