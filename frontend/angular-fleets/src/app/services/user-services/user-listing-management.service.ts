import {DestroyRef, inject, Injectable} from '@angular/core';
import {HttpClient, HttpStatusCode} from "@angular/common/http";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {Observable, tap} from "rxjs";
import {environment} from "../../../environments/environment";
import {UpdateListingRequest} from "../../models/group-listing/update-listing-request";
import {
  ListingViewInteractionsService
} from "../facade-services/listing-view-interactions/listing-view-interactions.service";

@Injectable({
  providedIn: 'root'
})
export class UserListingManagementService {
  private destroyRef = inject(DestroyRef)

  private createListingUrl = `${environment.apiBaseUrl}/group-listings/create_listing`;
  private deleteListingUrl = `${environment.apiBaseUrl}/group-listings/delete_listing`;
  private updateListingUrl = `${environment.apiBaseUrl}/group-listings/update_listing`;

  constructor(private httpClient: HttpClient, private listingInteract: ListingViewInteractionsService) {}

  createListing(createListingRequest: CreateListingRequest): Observable<any> {
    return this.httpClient.post<any>(this.createListingUrl, createListingRequest);
  }

  deleteListing(listingId: number) {
    return this.httpClient.delete<any>(`${this.deleteListingUrl}/${listingId}`);
  }

  updateListing(updateListingRequest: UpdateListingRequest): Observable<any> {
    return this.httpClient.put<any>(this.updateListingUrl, updateListingRequest);
  }
}
