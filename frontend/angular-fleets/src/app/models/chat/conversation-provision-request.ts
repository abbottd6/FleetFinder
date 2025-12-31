export class ConversationProvisionRequest {
  constructor(listingTitle: string, recipientId: number) {
    Object.assign(this, {
      convType: 'DIRECT',
      title: listingTitle,
      recipientId: recipientId
    })
  }
}
