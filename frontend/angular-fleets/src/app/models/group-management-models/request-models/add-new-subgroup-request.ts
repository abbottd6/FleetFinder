import {DropListOrientation} from "@angular/cdk/drag-drop";
import {RoleClassSummaryViewModel} from "../nested-models/role-class-summary-view-model";

export class AddNewSubgroupRequest {
  constructor(listingId: number,
              rootSubgroupId: number | null,
              parentSubgroupId: number | null,
              subgroupLabel: string,
              subgroupNotes: string | null,
              dropListOrientation: DropListOrientation,
              positions: RoleClassSummaryViewModel[]) {
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
