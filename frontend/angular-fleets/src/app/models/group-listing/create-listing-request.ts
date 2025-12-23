import {environment} from "../../../environments/environment";

export class CreateListingRequest {
  constructor(formData: any) {
    Object.assign(this, {
      listingTitle: formData.titleGroup.listingTitle,
      serverId: formData.sessionEnvInfoGroup.serverRegion,
      environmentId: formData.sessionEnvInfoGroup.gameEnvironment,
      experienceId: formData.sessionEnvInfoGroup.gameExperience,
      playStyleId: formData.gameplayInfoGroup.playStyle,
      categoryId: formData.gameplayInfoGroup.category,
      subcategoryId: formData.gameplayInfoGroup.subcategory,
      legalityId: formData.gameplayInfoGroup.legality,
      pvpStatusId: formData.gameplayInfoGroup.pvpStatus,
      systemId: formData.gameplayInfoGroup.planetarySystem,
      planetId: formData.gameplayInfoGroup.planetMoon,
      listingDescription: formData.gameplayInfoGroup.listingDescription,
      groupStatusId: formData.groupSpecInfoGroup.groupStatus,
      eventDate: this.toDateOnlyString(formData.groupSpecInfoGroup.eventScheduleDate),
      eventTime: formData.groupSpecInfoGroup.eventScheduleTime,
      eventTimeZone: formData.groupSpecInfoGroup.eventScheduleZone,
      currentPartySize: formData.groupSpecInfoGroup.currentPartySize,
      desiredPartySize: formData.groupSpecInfoGroup.desiredPartySize,
      availableRoles: formData.groupSpecInfoGroup.availableRoles,
      commsOption: formData.groupSpecInfoGroup.commsOption,
      commsService: formData.groupSpecInfoGroup.commsService,
    })
  }


  private toDateOnlyString(date: Date): String | null {
    if(date == null) {
      return null;
    }

    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
