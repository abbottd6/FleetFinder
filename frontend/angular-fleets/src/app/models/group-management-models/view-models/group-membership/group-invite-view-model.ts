import {InviteUserSummaryViewModel} from "../../nested-models/invite-user-summary-view-model";
import {GroupListingViewModel} from "../../../group-listing/group-listing-view-model";

export class GroupInviteViewModel {
  constructor(
    public inviteId: number,
    public senderSummary: InviteUserSummaryViewModel,
    public recipientSummary: InviteUserSummaryViewModel,
    public listingDetails: GroupListingViewModel,

  ){}
}
