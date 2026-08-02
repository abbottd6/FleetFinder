import {GroupCompSubgroupViewModel} from "./group-comp-subgroup-view-model";
import {GroupCompCrewPositionViewModel} from "./group-comp-crew-position-view-model";

export class GroupCompositionDto {
  constructor(
    public groupId: number,
    public subgroups: GroupCompSubgroupViewModel[],
  ){}
}
