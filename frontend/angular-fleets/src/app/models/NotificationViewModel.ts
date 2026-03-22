import {NotificationTargetMetadataModel} from "./NotificationTargetMetadataModel";

export class NotificationViewModel {

  constructor(public notificationId: number,
              public type: string,
              public title: string,
              public message: string,
              public targetMetadata: NotificationTargetMetadataModel | null,
              public createdAt: Date) {}
}
