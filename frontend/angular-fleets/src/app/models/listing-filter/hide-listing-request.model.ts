import {ListingFilterState} from "../../services/api-services/filter-api/filter.service";

export class HideListingRequest {
  constructor(groupId: number) {
    Object.assign(this, {
      groupId: groupId
    })
  }
}
