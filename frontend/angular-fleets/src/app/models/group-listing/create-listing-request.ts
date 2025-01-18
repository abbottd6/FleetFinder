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
      eventScheduleDate: formData.groupSpecInfoGroup.eventScheduleDate,
      currentPartySize: formData.groupSpecInfoGroup.currentPartySize,
      desiredPartySize: formData.groupSpecInfoGroup.desiredPartySize,
      availableRoles: formData.groupSpecInfoGroup.availableRoles,
      commsOption: formData.groupSpecInfoGroup.commsOption.option,
      commsService: formData.groupSpecInfoGroup.commsService,
    })
  }
}
