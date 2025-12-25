import { Injectable } from '@angular/core';
import {environment} from "../../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {Page} from "./group-listings-fetch-api/group-listing-fetch.service";
import {ListingTemplateViewModel} from "../../models/listing-templates/listing-template-view-model";
import {Observable, tap} from "rxjs";
import {CreateTemplateRequest} from "../../models/listing-templates/create-template-request";

class Observerable<T> {
}

@Injectable({
  providedIn: 'root'
})
export class ListingTemplatesApiService {

  private getUrl = `${environment.apiBaseUrl}/users/my/templates/get`;
  private createUrl = `${environment.apiBaseUrl}/users/my/templates/save`;
  private deleteUrl = `${environment.apiBaseUrl}/users/my/templates/delete`;

  constructor(private httpClient: HttpClient) {}

  getTemplates(
    page: number,
    size: number,
    sortField: string,
    sortDirection: string): Observerable<Page<ListingTemplateViewModel>> {
    const requestBody = {
      page,
      size,
      sortField,
      sortDirection,
    };

    return this.httpClient.post<Page<ListingTemplateViewModel>>(`${this.getUrl}`, requestBody)
  }

  createTemplate(request: CreateTemplateRequest): Observable<any> {
    console.log("url: ", this.getUrl, ", request: ", request);

    return this.httpClient.post<any>(this.createUrl, request).pipe(
      tap(response => {
        console.log("Whats goin on here? ", response);
      })
    );
  }

  deleteTemplate(templateId: number) {
    return this.httpClient.delete<any>(`${this.deleteUrl}/${templateId}`)
  }
}
