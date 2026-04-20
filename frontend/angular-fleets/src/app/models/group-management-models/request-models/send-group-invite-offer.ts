import {UserMonikerSummaryViewModel} from "../nested-models/user-moniker-summary-view-model";
import {RoleClassSummaryViewModel} from "../nested-models/role-class-summary-view-model";

export class SendGroupInviteOffer {
  constructor(listingId: number, recipient: UserMonikerSummaryViewModel, rosterClass: string,
              roleSummary: RoleClassSummaryViewModel, message: string, expiresAt: Date){
    Object.assign(this, {
      listingId: listingId,
      recipientSummary: recipient,
      memberStatus: rosterClass,
      roleSummary: roleSummary,
      inviteMessage: message,
      expiresAt: expiresAt
    })
  }
}
