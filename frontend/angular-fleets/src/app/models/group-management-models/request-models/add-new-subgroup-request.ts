import {DropListOrientation} from "@angular/cdk/drag-drop";
import {GroupCompCrewPositionViewModel} from "../view-models/group-composition/group-comp-crew-position-view-model";

export class AddNewSubgroupRequest {
  constructor(listingId: number,
              rootSubgroupId: number | null,
              parentSubgroupId: number | null,
              subgroupLabel: string,
              subgroupNotes: string | null,
              dropListOrientation: DropListOrientation,
              positions: GroupCompCrewPositionViewModel[]) {
    Object.assign(this, {
      listingId: listingId,
      rootSubgroupId: rootSubgroupId,
      parentSubgroupId: parentSubgroupId,
      subgroupLabel: subgroupLabel,
      subgroupNotes: subgroupNotes,
      dropListOrientation: dropListOrientation,
      positions: positions
    })
  }
}
