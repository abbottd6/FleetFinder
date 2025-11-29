import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";
import {UpdateListingRequest} from "../../models/group-listing/update-listing-request";
import {AddBookmarkRequest} from "../../models/bookmark-requests/add-bookmark-request";

@Injectable({
  providedIn: 'root'
})
export class UserListingService {

  private createListingUrl = `${environment.apiBaseUrl}/group-listings/create_listing`;
  private deleteListingUrl = `${environment.apiBaseUrl}/group-listings/delete_listing`;
  private updateListingUrl = `${environment.apiBaseUrl}/group-listings/update_listing`;
  private addBookmarkUrl = `${environment.apiBaseUrl}/users/user_add_bookmark`;

  constructor(private httpClient: HttpClient) {}

  createListing(createListingRequest: CreateListingRequest): Observable<any> {
    return this.httpClient.post<any>(this.createListingUrl, createListingRequest)
  }

  deleteListing(listingId: number) {
    return this.httpClient.delete<any>(`${this.deleteListingUrl}/${listingId}`);
  }

  updateListing(updateListingRequest: UpdateListingRequest): Observable<any> {
    return this.httpClient.put<any>(this.updateListingUrl, updateListingRequest);
  }

  addBookmark(request: AddBookmarkRequest): Observable<any> {
    return this.httpClient.post<any>(this.addBookmarkUrl, request)
  }
}
