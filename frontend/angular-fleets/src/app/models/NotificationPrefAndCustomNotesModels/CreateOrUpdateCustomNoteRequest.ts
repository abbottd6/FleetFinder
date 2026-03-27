import {CustomNotificationFormShape} from "../../services/custom-notification-form-service/custom-note-form.service";

export class CreateOrUpdateCustomNoteRequest {
  constructor(formData: CustomNotificationFormShape) {
    Object.assign(this, {
      tagLabel: formData.labelCtrl.value,
      serverId: formData.serverCtrl.value,
      environmentId: formData.environmentCtrl.value,
      experienceId: formData.experienceCtrl.value,
      categoryId: formData.categoryCtrl.value,
      subcategoryId: formData.subcategoryCtrl.value,
      systemId: formData.systemCtrl.value,
      languageCode: formData.languageCtrl.value,
      pvpStatusId: formData.pvpStatusCtrl.value,
      legalityId: formData.legalityCtrl.value,
      groupStatusId: formData.groupStatusCtrl.value,
    })
  }
}
