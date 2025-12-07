import {inject, Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";
import {AuthService} from "../auth/auth-services/auth.service";
import {HttpClient} from "@angular/common/http";
import {HideListingRequest} from "../../models/listing-filter/hide-listing-request.model";

@Injectable({
  providedIn: 'root'
})
export class HiddenListingsService {

  private hiddenUrl = `${environment.apiBaseUrl}/users/my/hidden`;
  private auth = inject(AuthService);

  constructor(private httpClient: HttpClient) {}

  addHidden(request: HideListingRequest) {
    return this.httpClient.post<any>(`${this.hiddenUrl}:add`, request)
  }


}
