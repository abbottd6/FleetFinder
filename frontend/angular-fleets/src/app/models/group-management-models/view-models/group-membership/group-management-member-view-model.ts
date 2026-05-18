import {UserMonikerSummaryViewModel} from "../../nested-models/user-moniker-summary-view-model";
import {MemberPositionSummaryViewModel} from "../../nested-models/member-position-summary-view-model";
import {InGroupRankViewModel} from "./in-group-rank-view-model";
import {UserFullMonikerSummary} from "../../nested-models/user-full-moniker-summary";

export class GroupManagementMemberViewModel {
  readonly entityType = 'member' as const;

  constructor(
    public listingId: number,
    public userSummary: UserFullMonikerSummary,
    public memberStatus: string,
    public memberNote: string,
    public memberRole: MemberPositionSummaryViewModel,
    public memberRank: InGroupRankViewModel,
    public hasMic: boolean,
    public hasHeadset: boolean,
    public hasExtNotes: boolean,
    public rsvpStatus: string,
    public joinedAt: Date
  ){}
}
