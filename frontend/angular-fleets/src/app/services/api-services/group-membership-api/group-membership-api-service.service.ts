import { Injectable } from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";

@Injectable({
  providedIn: 'root'
})
export class GroupMembershipApiServiceService {

  private baseUrl = `${environment.apiBaseUrl}/group-membership`;

  constructor(private httpClient: HttpClient) {}

  getMyGroupMemberships(): Observable<Page<GroupMembershipViewModel>>
}
