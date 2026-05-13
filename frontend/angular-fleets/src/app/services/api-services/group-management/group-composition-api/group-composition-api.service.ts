import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../../../environments/environment";
import {
  CrewTemplateViewModel
} from "../../../../models/group-management-models/view-models/group-composition/crew-template-view-model";
import {Observable} from "rxjs";
import {
  GroupCompositionDto
} from "../../../../models/group-management-models/view-models/group-composition/group-composition-dto";

@Injectable({
  providedIn: 'root'
})
export class GroupCompositionApiService {

  private baseUrl = `${environment.apiBaseUrl}/group-composition`;

  constructor(private httpClient: HttpClient) { }

  getExistingGroupStructure(groupId: number): Observable<GroupCompositionDto> {
    return this.httpClient.get<GroupCompositionDto>(
      `${this.baseUrl}/get-existing-group-structure/${groupId}`);
  }

  fetchCrewTemplateSummaries(): Observable<CrewTemplateViewModel[]> {
    return this.httpClient.get<CrewTemplateViewModel[]>(`${this.baseUrl}/my-crew-templates`);
  }

  createSubgroupFromTemplate(groupId: number, template: CrewTemplateViewModel): Observable<GroupCompositionDto> {
    return this.httpClient.post<GroupCompositionDto>(
      `${this.baseUrl}/create-subgroup-from-template/${groupId}`, template);
  }

  deleteSubgroup(groupId: number, subgroupId: number) {
    return this.httpClient.delete<void>(`${this.baseUrl}/delete-subgroup/${groupId}/${subgroupId}`);
  }
}
