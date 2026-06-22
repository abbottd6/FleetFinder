import {GroupCompSubgroupViewModel} from "../view-models/group-composition/group-comp-subgroup-view-model";
import {RoleClassSummaryViewModel} from "../nested-models/role-class-summary-view-model";

export class NewCrewPositionRequest {
  constructor(subgroup: GroupCompSubgroupViewModel, role: RoleClassSummaryViewModel, positionNote: string | null){
    Object.assign(this, {
      groupId: subgroup.listingId,
      subgroupId: subgroup.subgroupId,
      roleId: role.roleId,
      positionNote: positionNote
    })
  }
}
