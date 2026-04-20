import {UserRole} from "../../services/user-services/user.service";

export class PublicUser {

  constructor(public userId: number,
              public username: string,
              public discordUsername: string,
              public inGameUsername: string,
              public lastAccess: Date
  ) {}
}
