import {environment} from "../../../environments/environment";

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
      //Combining users date, time, and time zone
      //converting to UTC to store in db
      //Listing data to be displayed in users' time zone
      eventSchedule: UpdateListingRequest.dateTimeTZConcatenation(
        formData.groupSpecInfoGroup.eventScheduleDate,
        formData.groupSpecInfoGroup.eventScheduleTime,
        formData.groupSpecInfoGroup.eventScheduleZone
      ),
      categoryId: formData.gameplayInfoGroup.category,
      subcategoryId: formData.gameplayInfoGroup.subcategory,
      pvpStatusId: formData.gameplayInfoGroup.pvpStatus,
      systemId: formData.gameplayInfoGroup.planetarySystem,
      planetId: formData.gameplayInfoGroup.planetMoon,
      listingDescription: formData.gameplayInfoGroup.listingDescription,
      desiredPartySize: formData.groupSpecInfoGroup.desiredPartySize,
      currentPartySize: formData.groupSpecInfoGroup.currentPartySize,
      availableRoles: formData.groupSpecInfoGroup.availableRoles,
      commsOption: formData.groupSpecInfoGroup.commsOption.option,
      commsService: formData.groupSpecInfoGroup.commsService,
    })
  }

  //combining selections from date picker, time dropdown, time zone dropdown for single field submission
  static dateTimeTZConcatenation(date: Date, time: string, timezone: string): string | null {
    if (!date || !time || !timezone) {
      return null;
    }

    //extract date from datepicker javascript date
    const dateString = date.toISOString().split('T')[0];
    if(!environment.production) {
      console.log("Javascript date string extracted: ", dateString)
    }

    //combining date and time into ISO acceptable string
    const dateTimeString = `${dateString}T${time}:00`;

    //converting combined date/time to selected time zone
    //necessary in case selected zone differs from users browser time
    const combinedDateTimeInSelectedTZ = new Date(
      new Date(dateTimeString).toLocaleString('sv-SE', {timeZone: timezone})
    );

    //converting the date to ISO string in UTC time for database standard
    return combinedDateTimeInSelectedTZ.toISOString();
  }
}
