export class GroupListing {

  constructor(public groupId: number,
              //MIGHT NEED TO DO SOMETHING ABOUT THIS, JPA ENTITY USES "USER" type
              public userId: number,
              public server: {
                serverId: number;
                serverName: string,
                },
              public environment: {
                environmentId: number;
                environmentType: String;
              },
              public experience: {
                experienceId: number;
                experienceType: string;
              },
              public listingTitle: string,
              public listingUser: string,
              public gameplayStyle: {
                styleId: number;
                playStyle: string;
              },
              public legality: {
                legalityId: number;
                legality: string;
              },
              public groupStatus: {
                groupStatusId: number;
                groupStatus: string;
              },
              public eventSchedule: Date,
              public gameplayCategory: {
                categoryId: number;
                categoryName: string;
              },
              public gameplaySubcategory: {
                subcategoryId: number;
                subcategoryName: string;
              },
              public pvpStatusId: number,
              public pvpStatus: string,
              public systemId: number,
              public systemName: string,
              public planetId: number,
              public planetName: string,
              public listingDescription: string,
              public desiredPartySize: number,
              public availableRoles: string,
              public commsOptions: string,
              public commsService: string,
              public creationTimestamp: Date,
              public updateTimestamp: Date
      ){

  }
}
