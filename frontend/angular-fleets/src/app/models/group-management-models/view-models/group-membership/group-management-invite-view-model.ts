import {UserMonikerSummaryViewModel} from "../../nested-models/user-moniker-summary-view-model";

export class GroupManagementInviteViewModel {
  constructor(
    public inviteId: number,
    public listingId: number,
    public senderSummary: UserMonikerSummaryViewModel,
    public recipientSummary: UserMonikerSummaryViewModel,
    public memberStatus: string,
    public inviteDirection: string,
    public inviteStatus: string,
    public inviteMessage: string,
    public hasMic: string,
    public hasHeadset: string,
    public expiresAt: Date,
    public sentAt: Date
  ){}
}
