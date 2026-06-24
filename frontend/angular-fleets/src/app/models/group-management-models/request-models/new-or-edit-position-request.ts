import {RoleClassSummaryViewModel} from "../nested-models/role-class-summary-view-model";

export class NewOrEditPositionRequest {
  constructor(listingId: number, subgroupId: number, role: RoleClassSummaryViewModel, positionNote: string | null){
    Object.assign(this, {
      groupId: listingId,
      subgroupId: subgroupId,
      roleId: role.roleId,
      positionNote: positionNote
    })
  }
}
