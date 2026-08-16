import {MasterRsvpViewModel} from "./master-rsvp-view-model";

export class MasterRsvpResponseWrapper {
  constructor(
    public hasGlobalMaster: boolean,
    public mastersList: MasterRsvpViewModel[]
  ){}
}
