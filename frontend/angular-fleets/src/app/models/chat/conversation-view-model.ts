export class ConversationViewModel {

  constructor(
    public conversationId: number,
    public conversationType: string,
    public conversationTitle: string,
    public otherUserId: number,
    public otherUserName: string,
    public createdAt: Date,
    public updatedAt: Date,
    public lastMsgId: number,
    public currentUserId: number,
    public currentUserName: string,
    public dmKey: string
  ){}
}
