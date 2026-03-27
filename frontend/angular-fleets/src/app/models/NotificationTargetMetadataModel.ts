export class NotificationTargetMetadataModel {
  constructor(
    public noteTopic: string,
    public targetId: number,
    public targetLabel: string,
    public targetCreatedAt: Date,
    public addContext: string
  ) {}
}
