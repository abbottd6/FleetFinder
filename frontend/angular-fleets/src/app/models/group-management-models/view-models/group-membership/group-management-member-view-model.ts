import {UserMonikerSummaryViewModel} from "../../nested-models/user-moniker-summary-view-model";
import {MemberPositionSummaryViewModel} from "../../nested-models/member-position-summary-view-model";
import {InGroupRankViewModel} from "./in-group-rank-view-model";

export class GroupManagementMemberViewModel {
  constructor(
    public listingId: number,
    public userSummary: UserMonikerSummaryViewModel,
    public memberStatus: string,
    public memberNote: string,
    public memberRole: MemberPositionSummaryViewModel,
    public memberRank: InGroupRankViewModel,
    public hasComms: boolean,
    public hasExtNotes: boolean,
    public rsvpStatus: string,
    public joinedAt: Date
  ){}
}
