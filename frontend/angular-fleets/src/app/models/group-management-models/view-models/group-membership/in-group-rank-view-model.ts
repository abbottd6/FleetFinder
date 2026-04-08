import {UserMonikerSummaryViewModel} from "../../nested-models/user-moniker-summary-view-model";

export class InGroupRankViewModel {
  constructor(
    public rankId: number,
    public listingId: number,
    public rankSubgroupScope: number,
    public rankTitle: string,
    public rankNotes: string,
    public createdByUser: UserMonikerSummaryViewModel,
    public createdAt: Date
  ){}
}
