import {DestroyRef, inject, Injectable} from '@angular/core';
import {environment} from "../../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {
  MasterRsvpResponseWrapper
} from "../../../models/group-management-models/view-models/management-rsvp/master-rsvp-response-wrapper";

@Injectable({
  providedIn: 'root'
})
export class ManagementRsvpApiService {
  private destroyRef = inject(DestroyRef);

  private baseUrl = `${environment.apiBaseUrl}/group-rsvp`;

  constructor(private httpClient: HttpClient) {}

  getRsvpActiveMastersList(listingId: number): Observable<MasterRsvpResponseWrapper> {
    return this.httpClient.get<MasterRsvpResponseWrapper>(`${this.baseUrl}/masters-list/${listingId}`);
  }
}
