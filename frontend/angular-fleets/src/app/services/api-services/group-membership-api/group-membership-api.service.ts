  import {DestroyRef, inject, Injectable} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Page} from "../group-listings-fetch-api/group-listing-fetch.service";
import {Observable} from "rxjs";
import {
  GroupMembershipViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-membership-view-model";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
  import {tap} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class GroupMembershipApiService {
  private destroyRef = inject(DestroyRef);

  private baseUrl = `${environment.apiBaseUrl}/group-membership`;

  constructor(private httpClient: HttpClient) {}

  getMyGroupMemberships(): Observable<Page<GroupMembershipViewModel>> {
    return this.httpClient.get<Page<GroupMembershipViewModel>>(`${this.baseUrl}/my_groups`).pipe(
      tap(response => {
        console.log('DTO: ' + response);
      })
  )

  }
}
