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
import {
  GroupCompCrewPositionViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-crew-position-view-model";
import {
  UpdateSubgroupDropListOrientationRequest
} from "../../../../models/group-management-models/request-models/update-subgroup-drop-list-orientation-request";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {
  GroupCompSubgroupViewModel
} from "../../../../models/group-management-models/view-models/group-composition/group-comp-subgroup-view-model";

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

  updateGroupCompositionState(groupCompDto: GroupCompositionDto): Observable<void> {
    return this.httpClient.put<void>(`${this.baseUrl}/update-group-composition-tree`, groupCompDto);
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

  updateSubgroupDropListOrientation(requestDto: UpdateSubgroupDropListOrientationRequest) {
    return this.httpClient.patch<void>(`${this.baseUrl}/update-subgroup-orientation`, requestDto);
  }

  updateSubgroupLabel(subgroupId: number, newLabel: string): Observable<void> {
    return this.httpClient.patch<void>(`${this.baseUrl}/update-subgroup-label/${subgroupId}`, newLabel);
  }

  assignMemberToPosition(position: GroupCompCrewPositionViewModel): Observable<number> {
    return this.httpClient.patch<number>(`${this.baseUrl}/assign-member-position`, position);
  }

  clearMemberPositionAssignment(position: GroupCompCrewPositionViewModel): Observable<number> {
    return this.httpClient.patch<number>(`${this.baseUrl}/clear-member-position-assignment`, position);
  }

  clearPositionAssignmentByMember(member: GroupManagementMemberViewModel): Observable<GroupManagementMemberViewModel> {
    return this.httpClient.patch<GroupManagementMemberViewModel>(
      `${this.baseUrl}/clear-assignment-by-member`, member
    );
  }
}
