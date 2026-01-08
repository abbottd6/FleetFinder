export class ModListingActionViewModel {

  constructor(
    public actionId: number,
    public archiveId: number | null,
    public groupId: number | null,
    public userId: number,
    public username: string,
    public modId: number | null,
    public modName: string | null,
    public actionType: string,
    public actionNote: string | null,
    public actionTs: Date,
  ){}
}
