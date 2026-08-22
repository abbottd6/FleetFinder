export class MasterRsvpViewModel {
  constructor(
    public idRsvpMaster: number,
    public listingId: number,
    public subgroupId: number | null,
    public subgroupLabel: string | null,
    public subgroupIsSource: boolean,
    public scheduledTime: Date,
    public expiresAt: Date,
    public rsvpMessage: string | null,
    public commsShare: string | null,
    public batchedAndSent: boolean,
    public createdAt: Date
  ){}
}
