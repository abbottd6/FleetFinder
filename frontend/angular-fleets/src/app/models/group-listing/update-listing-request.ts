import {environment} from "../../../environments/environment";
import { DateTime } from 'luxon';

export class UpdateListingRequest {
  constructor(formData: any, gId: number) {
    if (formData.groupStatus != 2) {
      formData.eventScheduleDate = null;
      formData.eventScheduleTime = null;
      formData.eventScheduleZone = null;
    }
    Object.assign(this, {
      groupId: gId,
      userId: null,
      serverId: formData.sessionEnvInfoGroup.serverRegion,
      environmentId: formData.sessionEnvInfoGroup.gameEnvironment,
      experienceId: formData.sessionEnvInfoGroup.gameExperience,
      listingTitle: formData.titleGroup.listingTitle,
      playStyleId: formData.gameplayInfoGroup.playStyle,
      legalityId: formData.gameplayInfoGroup.legality,
      groupStatusId: formData.groupSpecInfoGroup.groupStatus,
      eventDate: this.toDateOnlyString(formData.groupSpecInfoGroup.eventScheduleDate),
      eventTime: formData.groupSpecInfoGroup.eventScheduleTime,
      eventTimeZone: formData.groupSpecInfoGroup.eventScheduleZone,
      categoryId: formData.gameplayInfoGroup.category,
      subcategoryId: formData.gameplayInfoGroup.subcategory,
      pvpStatusId: formData.gameplayInfoGroup.pvpStatus,
      systemId: formData.gameplayInfoGroup.planetarySystem,
      planetId: formData.gameplayInfoGroup.planetMoon,
      listingDescription: formData.gameplayInfoGroup.listingDescription,
      desiredPartySize: formData.groupSpecInfoGroup.desiredPartySize,
      currentPartySize: formData.groupSpecInfoGroup.currentPartySize,
      availableRoles: formData.groupSpecInfoGroup.availableRoles,
      commsOption: formData.groupSpecInfoGroup.commsOption,
      commsService: formData.groupSpecInfoGroup.commsService,
    })
  }

  private toDateOnlyString(date: Date): String {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
