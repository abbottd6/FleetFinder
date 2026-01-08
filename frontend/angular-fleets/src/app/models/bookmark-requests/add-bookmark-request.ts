export class AddBookmarkRequest {
  constructor(groupId: number) {
    Object.assign(this, {
      groupId: groupId,
    })
  }
}
