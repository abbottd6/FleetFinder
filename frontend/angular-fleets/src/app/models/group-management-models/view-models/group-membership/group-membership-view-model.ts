import {UserMonikerSummaryViewModel} from "../../nested-models/user-moniker-summary-view-model";
import {InGroupRankViewModel} from "./in-group-rank-view-model";
import {GroupListingViewModel} from "../../../group-listing/group-listing-view-model";
import {MemberPositionSummaryViewModel} from "../../nested-models/member-position-summary-view-model";

export class GroupMembershipViewModel {
  constructor(
    public userSummary: UserMonikerSummaryViewModel,
    public memberStatus: string,
    public memberRole: MemberPositionSummaryViewModel,
    public memberRank: InGroupRankViewModel,
    public memberNote: string,
    public hasMic: boolean,
    public hasHeadset: boolean,
    public hasExtNotes: boolean,
    public rsvpStatus: string,
    public joinedAt: Date,
    public isAuthorizedManager: boolean,
    public listing: GroupListingViewModel
  ){}
}
