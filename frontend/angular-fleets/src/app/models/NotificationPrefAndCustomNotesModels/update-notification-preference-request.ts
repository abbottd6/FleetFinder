export class UpdateNotificationPreferenceRequest {
  constructor(label: string, value: boolean) {
    Object.assign(this, {
      label: label,
      value: value
    })
  }
}
