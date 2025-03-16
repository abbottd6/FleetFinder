export class CreateListingRequest {
  constructor(public userId: number, formData: any) {
    Object.assign(this, {
      userId: userId,
      listingTitle: formData.titleGroup.listingTitle,
      serverId: formData.sessionEnvInfoGroup.serverRegion,
      environmentId: formData.sessionEnvInfoGroup.gameEnvironment,
      experienceId: formData.sessionEnvInfoGroup.gameExperience,
      playStyleId: formData.gameplayInfoGroup.playStyle,
      categoryId: formData.gameplayInfoGroup.category.gameplayCategoryId,
      subcategoryId: formData.gameplayInfoGroup.subcategory,
      legalityId: formData.gameplayInfoGroup.legality,
      pvpStatusId: formData.gameplayInfoGroup.pvpStatus,
      systemId: formData.gameplayInfoGroup.planetarySystem.systemId,
      planetId: formData.gameplayInfoGroup.planetMoon,
      listingDescription: formData.gameplayInfoGroup.listingDescription,
      groupStatusId: formData.groupSpecInfoGroup.groupStatus,

      //Combining users date, time, and time zone
      //converting to UTC to store in db
      //Listing data to be displayed in users' time zone
      eventSchedule: CreateListingRequest.dateTimeTZConcatenation(
        formData.groupSpecInfoGroup.eventScheduleDate,
        formData.groupSpecInfoGroup.eventScheduleTime,
        formData.groupSpecInfoGroup.eventScheduleZone
      ),

      currentPartySize: formData.groupSpecInfoGroup.currentPartySize,
      desiredPartySize: formData.groupSpecInfoGroup.desiredPartySize,
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
    console.log("Javascript date string extracted: ", dateString)

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
