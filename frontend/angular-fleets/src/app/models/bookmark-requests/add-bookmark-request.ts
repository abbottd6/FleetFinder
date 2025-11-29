export class AddBookmarkRequest {
  constructor(groupId: number) {
    Object.assign(this, {
      userId: null,
      groupId: groupId,
    })
  }
}
