import {GroupCompSubgroupViewModel} from "../view-models/group-composition/group-comp-subgroup-view-model";

export class TemplateFromCompRequest {
  constructor(listingId: number, templateLabel: string, subgroups: GroupCompSubgroupViewModel[]) {
    Object.assign(this, {
      groupId: listingId,
      templateLabel: templateLabel,
      subgroups: subgroups
    })
  }
}
