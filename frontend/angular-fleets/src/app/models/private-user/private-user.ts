import { GroupListingViewModel } from '../group-listing/group-listing-view-model'

export class PrivateUser {

  constructor(public userId: number,
              public username: string,
              public email: string,
              public server: string,
              public org: string,
              public about: string,
              public acctCreated: Date,
              public groupListingsDto: GroupListingViewModel[],
              public roles: string[]) {}
}
