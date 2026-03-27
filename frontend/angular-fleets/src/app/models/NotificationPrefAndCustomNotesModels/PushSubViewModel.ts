export class PushSubViewModel {

  constructor(
    public idPushSub: number,
    public userId: number,
    public userLabel: string,
    public sysNotesEnabled: boolean,
    public groupNotesEnabled: boolean,
    public socialNotesEnabled: boolean,
    public dailyFailureCount: number,
    public createdAt: Date,
  ){}
}
