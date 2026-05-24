import {GroupCompSubgroupViewModel} from "./group-comp-subgroup-view-model";
import {GroupCompCrewPositionViewModel} from "./group-comp-crew-position-view-model";

export class GroupCompositionDto {
  constructor(
    public subgroups: GroupCompSubgroupViewModel[],
    public crewPositions: GroupCompCrewPositionViewModel[],
  ){}
}
