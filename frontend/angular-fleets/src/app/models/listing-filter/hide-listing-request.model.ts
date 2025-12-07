import {ListingFilterState} from "../../services/api-lookup-services/filter.service";

export class HideListingRequest {
  constructor(groupId: number) {
    Object.assign(this, {
      groupId: groupId
    })
  }
}
