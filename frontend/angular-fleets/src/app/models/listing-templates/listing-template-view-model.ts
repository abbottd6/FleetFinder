import { LanguageCode } from "../language-options";
import {ListingDiscoveryOptions} from "../group-listing/group-listing-view-model";

export class ListingTemplateViewModel {

  constructor (
    public templateId: number,
    public userId: number,
    public serverId: number,
    public server: string,
    public environmentId: number,
    public environment: string,
    public experienceId: number,
    public experience: string,
    public listingTitle: string,
    public styleId: number,
    public playStyle: string,
    public legalityId: number,
    public legality: string,
    public groupStatusId: number,
    public groupStatus: string,
    public eventSchedule: Date,
    public categoryId: number,
    public category: string,
    public subcategoryId: number,
    public subcategory: string,
    public pvpStatusId: number,
    public pvpStatus: string,
    public systemId: number,
    public system: string,
    public planetId: number,
    public planetMoonSystem: string,
    public listingDescription: string,
    public desiredPartySize: number,
    public currentPartySize: number,
    public availableRoles: string,
    public commsOption: string,
    public commsService: string,
    public languageCode: LanguageCode,
    public joinRequestPrompt: string,
    public discovery: ListingDiscoveryOptions,
    public creationTimestamp: Date,
  ){}
}
