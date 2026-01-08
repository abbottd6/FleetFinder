import {ConversationViewModel} from "./conversation-view-model";

export class SendMessageRequest {

  constructor(conv: ConversationViewModel, userId: number, type: string, msg: string) {
    Object.assign(this, {
      conversationId: conv.conversationId,
      senderId: userId,
      messageType: type,
      msgBody: msg,
      repliedToMsgId: conv.lastMsgId,
      clientMessageId: crypto.randomUUID()
    })
  }
}
