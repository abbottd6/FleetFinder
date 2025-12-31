export class MessageViewModel {

  constructor(
    public msgId: number,
    public conversationId: number,
    public senderId: number,
    public senderName: string,
    public messageType: string,
    public msgBody: string,
    public createdAt: Date,
    public updatedAt: Date,
    public deletedAt: Date,
    public repliedToMessageId: number,
    public clientMessageId: string
  ){}
}
