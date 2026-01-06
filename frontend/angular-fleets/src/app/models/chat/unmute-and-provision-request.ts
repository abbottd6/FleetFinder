import {ConversationProvisionRequest} from "./conversation-provision-request";
import {UnmuteDto} from "../../services/facade-services/chat/chat-host.service";

export class UnmuteAndProvisionRequest {
  constructor(provRequest: ConversationProvisionRequest, unmute: UnmuteDto) {
    Object.assign(this, {
      convType: provRequest.convType,
      title: provRequest.title,
      recipientId: provRequest.recipientId,
      otherUsername: unmute.otherUsername,
      conversationId: unmute.conversationId,
    })
  }
}
