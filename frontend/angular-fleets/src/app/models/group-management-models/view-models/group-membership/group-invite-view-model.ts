import {UserMonikerSummaryViewModel} from "../../nested-models/user-moniker-summary-view-model";
import {GroupListingViewModel} from "../../../group-listing/group-listing-view-model";

export class GroupInviteViewModel {
  constructor(
    public inviteId: number,
    public senderSummary: UserMonikerSummaryViewModel,
    public recipientSummary: UserMonikerSummaryViewModel,
    public listingDetails: GroupListingViewModel,
    public memberStatus: string,
    public inviteDirection: string,
    public inviteStatus: string,
    public inviteMessage: string,
    public expiresAt: Date,
    public sentAt: Date
  ){}
}
