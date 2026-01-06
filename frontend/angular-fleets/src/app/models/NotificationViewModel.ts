export class NotificationViewModel {

  constructor(public notificationId: number,
              public type: string,
              public title: string,
              public message: string,
              public createdAt: Date) {}
}
