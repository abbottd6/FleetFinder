import { GroupListingViewModel } from '../group-listing/group-listing-view-model'

export class PublicUser {

  constructor(public userId: number,
              public username: string,
              public server: string,
              public org: string,
              public about: string) {}
}
