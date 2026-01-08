export class ConversationProvisionRequest {
  convType!: 'DIRECT' | 'GROUP';
  title!: string;
  recipientId!: number;

  constructor(listingTitle: string, recipientId: number) {
    Object.assign(this, {
      convType: 'DIRECT',
      title: listingTitle,
      recipientId: recipientId
    })
  }
}
