import { GroupListingViewModel } from '../group-listing/group-listing-view-model'

export class PrivateUser {

  constructor(public userId: number,
              public username: string,
              public server: string,
              public org: string,
              public about: string,
              public acctCreated: Date,
              public lastAccess: Date,
              public discordId: string,
              public externalSysNotesEnabled: boolean,
              public externalGroupNotesEnabled: boolean,
              public externalSocialNotesEnabled: boolean,
              public groupListingsDto: GroupListingViewModel[]) {}
}
