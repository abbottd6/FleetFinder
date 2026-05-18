export class UserFullMonikerSummary {

  constructor(
    public userId: number,
    public username: string,
    public inGameUsername: string,
    public discordUsername: string,
    public lastAccess: Date
  ){}
}
