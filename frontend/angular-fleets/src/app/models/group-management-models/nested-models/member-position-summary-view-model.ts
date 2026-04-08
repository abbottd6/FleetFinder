import {RoleClassSummaryViewModel} from "./role-class-summary-view-model";
import {SubgroupSummaryViewModel} from "./subgroup-summary-view-model";

export class MemberPositionSummaryViewModel {
  constructor(
    public positionId: number,
    public listingId: number,
    public subgroupSummary: SubgroupSummaryViewModel,
    public roleSummary: RoleClassSummaryViewModel,
    public positionNote: string,
    public filledAt: Date,
    public createdAt: Date
  ){}
}
