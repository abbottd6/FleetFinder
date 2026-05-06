import {GroupCompCrewPositionViewModel} from "./group-comp-crew-position-view-model";

export class GroupCompSubgroupViewModel {
  constructor(
    public subgroupId: number,
    public subgroupLabel: string,
    public subgroupNotes: string,
    public sortOrder: number,
    public listingId: number,
    public rootSubgroupId: number,
    public parentSubgroupId: number,
    public createdAt: Date,
    public subgroups: GroupCompSubgroupViewModel[],
    public crewPositions: GroupCompCrewPositionViewModel[]
  ){}
}
