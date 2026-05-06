import {GroupCompSubgroupViewModel} from "./group-comp-subgroup-view-model";

export class GroupCompositionDto {
  constructor(
    public subgroups: GroupCompSubgroupViewModel[]
  ){}
}
