export class NotificationTargetMetadataModel {
  constructor(
    public noteTopic: string,
    public targetId: number,
    public targetLabel: string,
    public targetStatus: string,
    public targetCreatedAt: Date,
    public contextElementLabel: string,
    public contextElementStatus: string,
    public contextElementDate: Date,
    public addContext: string
  ) {}
}
