export class SendGroupInviteRequest {
  constructor(listingId: number, inGame: string, requestedStatus: string, message: string | null) {
    Object.assign(this, {
      listingId: listingId,
      inGameUsername: inGame,
      memberStatus: requestedStatus,
      requestMessage: message
    })
  }
}
