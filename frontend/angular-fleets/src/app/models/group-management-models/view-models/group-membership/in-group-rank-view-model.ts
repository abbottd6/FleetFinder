import {UserMonikerSummaryViewModel} from "../../nested-models/user-moniker-summary-view-model";

export class InGroupRankViewModel {
  constructor(
    public rankId: number,
    public listingId: number | null,
    public rankSubgroupScope: number | null,
    public rankTitle: string | null,
    public rankNotes: string | null,
    public createdByUser: UserMonikerSummaryViewModel | null,
    public createdAt: Date
  ){}
}
