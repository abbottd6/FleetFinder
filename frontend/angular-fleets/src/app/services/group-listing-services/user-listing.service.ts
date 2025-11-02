import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {DeleteListingRequest} from "../../models/group-listing/delete-listing-request";

@Injectable({
  providedIn: 'root'
})
export class UserListingService {

  private createListingUrl = `${environment.apiBaseUrl}/group-listings/create_listing`;
  private deleteListingUrl = `${environment.apiBaseUrl}/group-listings/delete_listing`;

  constructor(private httpClient: HttpClient) {}

  createListing(createListingRequest: CreateListingRequest): Observable<any> {
    return this.httpClient.post<any>(this.createListingUrl, createListingRequest)
  }

  deleteListing(deleteListingRequest: DeleteListingRequest): Observable<any> {
    return this.httpClient.delete<any>(this.deleteListingUrl, deleteListingRequest);
  }
}
