import {GroupCompCrewPositionViewModel} from "./group-comp-crew-position-view-model";
import {DropListOrientation} from "@angular/cdk/drag-drop";

export class GroupCompSubgroupViewModel {

  constructor(
    public entityType: string = 'subgroup',
    public subgroupId: number,
    public subgroupLabel: string,
    public subgroupNotes: string,
    public sortOrder: number,
    public dropListOrientation: DropListOrientation,
    public listingId: number,
    public rootSubgroupId: number,
    public parentSubgroupId: number,
    public createdAt: Date,
    public subgroups: GroupCompSubgroupViewModel[],
    public crewPositions: GroupCompCrewPositionViewModel[]
  ){}
}
