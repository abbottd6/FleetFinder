import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class CreateListingService {

  private createListingUrl = 'http://localhost:8080/api/groupListings/create_listing';

  constructor(private httpClient: HttpClient) {
  }

  createListing(createListingRequest: CreateListingRequest): Observable<any> {
    return this.httpClient.post<CreateListingRequest>(this.createListingUrl, createListingRequest)
  }
}
