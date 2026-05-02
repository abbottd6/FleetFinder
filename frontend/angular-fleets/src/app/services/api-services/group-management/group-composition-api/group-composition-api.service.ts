import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../../environments/environment";
import {
  CrewTemplateViewModel
} from "../../../../models/group-management-models/view-models/group-composition/crew-template-view-model";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GroupCompositionApiService {

  private baseUrl = `${environment.apiBaseUrl}/group-composition`;

  constructor(private httpClient: HttpClient) { }

  fetchCrewTemplateSummaries(): Observable<CrewTemplateViewModel[]> {
    return this.httpClient.get<CrewTemplateViewModel[]>(`${this.baseUrl}/my-crew-templates`);
  }

  //TODO RETURN TYPE
  createSubgroupFromTemplate(groupId: number, template: CrewTemplateViewModel): Observable<any> {
    return this.httpClient.post<any>(`${this.baseUrl}/create-subgroup-from-template/${groupId}`, template);
  }
}
