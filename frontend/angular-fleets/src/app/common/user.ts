import { GroupListing } from './group-listing'

export class User {

  constructor(public userId: number,
              public username: string,
              public email: string,
              public server: string,
              public org: string,
              public about: string,
              public acctCreated: Date,
              public groupListingsDto: GroupListing[]) {}
}
