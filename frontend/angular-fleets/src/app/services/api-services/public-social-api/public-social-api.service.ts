import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {PublicUser} from "../../../models/public-user/public-user";
import {environment} from "../../../../environments/environment";

import {Page} from "../../../models/page-interface";

@Injectable({
  providedIn: 'root'
})
export class PublicSocialApiService {

  constructor(private httpClient: HttpClient) {}

  searchUsers(searchCriteria: string): Observable<Page<PublicUser>> {
    const params = new HttpParams({ fromObject: { searchCriteria } })
    return this.httpClient.get<Page<PublicUser>>(`${environment.apiBaseUrl}/users/search_users`,
      { params });
  }
}
