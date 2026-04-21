import {
  InviteOfferFormShape
} from "../../../components/pop-ups/send-group-invite-popup/send-group-invite-popup.component";
import {FormGroup} from "@angular/forms";

export class SendGroupInviteOffer {
  constructor(inviteForm: FormGroup<InviteOfferFormShape>){
    Object.assign(this, {
      listingId: inviteForm.controls.listingCtrl.value?.groupId,
      recipientSummary: inviteForm.controls.recipientCtrl.value,
      memberStatus: inviteForm.controls.rosterClassCtrl.value?.toUpperCase(),
      roleSummary: inviteForm.controls.roleSummaryCtrl.value,
      inviteMessage: inviteForm.controls.messageCtrl.value,
      expiresAt: inviteForm.controls.expiryCtrl.value
    })
  }
}
