import {SessionUser} from "../../../services/user-services/user.service";

export class SendGroupInviteRequest {
  constructor(listingId: number, requestedStatus: string, message: string | null) {
    Object.assign(this, {
      listingId: listingId,
      memberStatus: requestedStatus,
      requestMessage: message
    })
  }
}
