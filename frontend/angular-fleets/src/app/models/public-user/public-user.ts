import {UserRole} from "../../services/user-services/user.service";

export class PublicUser {

  constructor(public userId: number,
              public username: string,
              public server: string,
              public org: string,
              public about: string) {}
}
