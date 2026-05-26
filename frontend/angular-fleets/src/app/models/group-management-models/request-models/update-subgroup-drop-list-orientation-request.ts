import {GroupCompSubgroupViewModel} from "../view-models/group-composition/group-comp-subgroup-view-model";

export class UpdateSubgroupDropListOrientationRequest {
  constructor(subgroup: GroupCompSubgroupViewModel){
    Object.assign(this, {
      groupId: subgroup.listingId,
      subgroupId: subgroup.subgroupId,
      orientation: subgroup.dropListOrientation
    })
  }
}
