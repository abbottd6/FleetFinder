export class SendGroupInviteRequest {
  constructor(listingId: number, inGame: string, requestedStatus: string, message: string | null,
              hasMic: boolean, hasHeadset: boolean) {
    Object.assign(this, {
      listingId: listingId,
      inGameUsername: inGame,
      memberStatus: requestedStatus,
      requestMessage: message,
      hasMic: hasMic,
      hasHeadset: hasHeadset
    })
  }
}
