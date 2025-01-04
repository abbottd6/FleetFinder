export class GroupListing {

  constructor(
              public groupId: number,
              public userId: number,
              public serverId: number,
              public server: string,
              public environmentId: number,
              public environment: string,
              public experienceId: number,
              public experience: string,
              public listingTitle: string,
              public userName: string,
              public styleId: number,
              public playStyle: string,
              public legalityId: number,
              public legality: string,
              public groupStatusId: number,
              public groupStatus: string,
              public eventSchedule: Date,
              public categoryId: number,
              public category: string,
              public subcategoryId: number,
              public subcategory: string,
              public pvpStatusId: number,
              public pvpStatus: string,
              public systemId: number,
              public system: string,
              public planetId: number,
              public planetMoonSystem: string,
              public listingDescription: string,
              public desiredPartySize: number,
              public availableRoles: string,
              public commsOption: string,
              public commsService: string,
              public creationTimestamp: Date,
              public lastUpdated: Date
      ){

  }
}
