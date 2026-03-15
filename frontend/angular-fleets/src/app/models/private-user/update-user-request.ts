export class UpdateUserRequest {
  constructor(server: string, org: string) {
    Object.assign(this, {
      serverId: server,
      org: org,
    })
  }
}
