import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {Observable} from "rxjs";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class CreateListingService {

  private createListingUrl = `${environment.apiBaseUrl}/api/group-listings/create_listing`;

  constructor(private httpClient: HttpClient) {
  }

  createListing(createListingRequest: CreateListingRequest): Observable<{ listingTitle: string}> {
    return this.httpClient.post<{ listingTitle: string }>(this.createListingUrl, createListingRequest)
  }
}
