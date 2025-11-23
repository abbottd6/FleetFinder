import {environment} from "../../../environments/environment";
import {ListingFilterState} from "../../services/api-lookup-services/filter.service";

export class ListingFilterRequest {
  constructor(filterData: ListingFilterState) {
    Object.assign(this, {
      search: filterData.searchInput ?? null,
      server: filterData.server?.id ?? null,
      environment: filterData.environment?.id ?? null,
      experience: filterData.experience?.id ?? null,
      playStyle: filterData.playStyle?.id ?? null,
      category: filterData.category?.id ?? null,
      subcategory: filterData.subcategory?.id ?? null,
      legality: filterData.legality?.id ?? null,
      pvpStatus: filterData.pvpStatus?.id ?? null,
      system: filterData.system?.id ?? null,
      planetMoonSystem: filterData.planetMoonSystem?.id ?? null,
      groupStatus: filterData.groupStatus?.id ?? null,
      eventSchedule: filterData.scheduleDate ?? null,
      commsOption: filterData.commsOption?.id ?? null,
    })

  }
}
