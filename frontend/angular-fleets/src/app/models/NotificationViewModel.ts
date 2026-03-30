import {NotificationTargetMetadataModel} from "./NotificationTargetMetadataModel";

export type ParentEntity = {
  parentEntityId: string,
  parentEntityType: string
}

export class NotificationViewModel {

  constructor(public notificationId: number,
              public type: string,
              public parentEntity: ParentEntity,
              public entityNewStatus: string,
              public title: string,
              public message: string,
              public targetMetadata: NotificationTargetMetadataModel | null,
              public createdAt: Date) {}
}
