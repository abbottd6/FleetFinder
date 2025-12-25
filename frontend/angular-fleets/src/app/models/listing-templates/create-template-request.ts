import {GroupListingViewModel} from "../group-listing/group-listing-view-model";

export class CreateTemplateRequest {
  constructor(listing: GroupListingViewModel) {
    Object.assign(this, {
      listingTitle: listing.listingTitle,
      serverId: listing.serverId,
      environmentId: listing.environmentId,
      experienceId: listing.experienceId,
      playStyleId: listing.styleId,
      categoryId: listing.categoryId,
      subcategoryId: listing.subcategoryId,
      legalityId: listing.legalityId,
      pvpStatusId: listing.pvpStatusId,
      systemId: listing.systemId,
      planetId: listing.planetId,
      listingDescription: listing.listingDescription,
      groupStatusId: listing.groupStatusId,
      eventDate: null,
      eventTime: null,
      eventTimeZone: null,
      currentPartySize: listing.currentPartySize,
      desiredPartySize: listing.desiredPartySize,
      availableRoles: listing.availableRoles,
      commsOption: listing.commsOption,
      commsService: listing.commsService,
    })
  }
}
