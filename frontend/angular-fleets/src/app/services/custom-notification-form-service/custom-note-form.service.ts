import {Injectable, OnDestroy} from '@angular/core';
import {FormControl, FormGroup, NonNullableFormBuilder, Validators} from "@angular/forms";
import {forkJoin, Subscription} from "rxjs";
import {LookupService} from "../api-services/reference-data-api/lookup.service";
import {LANGUAGE_OPTIONS, LanguageCode} from "../../models/language-options";
import {
  CustomNotificationViewModel
} from "../../models/NotificationPrefAndCustomNotesModels/CustomNotificationViewModel";

export type CustomNotificationFormShape = {
  labelCtrl: FormControl<string>;
  serverCtrl: FormControl<number | null>;
  environmentCtrl: FormControl<number | null>;
  experienceCtrl: FormControl<number | null>;
  categoryCtrl: FormControl<number | null>;
  subcategoryCtrl: FormControl<number | null>;
  systemCtrl: FormControl<number | null>;
  languageCtrl: FormControl<string | null>;
  pvpStatusCtrl: FormControl<number | null>;
  legalityCtrl: FormControl<number | null>;
  groupStatusCtrl: FormControl<number | null>;
}

@Injectable({
  providedIn: 'root'
})
export class CustomNoteFormService {

  customNotificationForm!: FormGroup<CustomNotificationFormShape>;

  constructor(private formBuilder: NonNullableFormBuilder, private lookup: LookupService) {
    this.customNotificationForm = this.buildForm();
  }

  private buildForm(): FormGroup<CustomNotificationFormShape> {
    return this.formBuilder.group<CustomNotificationFormShape>({
      labelCtrl: new FormControl<string>('', {
        validators: [Validators.required,
        Validators.minLength(3),
        Validators.maxLength(32)
        ],
        nonNullable: true
      }),
      serverCtrl: new FormControl(null),
      environmentCtrl: new FormControl(null),
      experienceCtrl: new FormControl(null),
      categoryCtrl: new FormControl(null),
      subcategoryCtrl: new FormControl(null),
      systemCtrl: new FormControl(null),
      languageCtrl: new FormControl(null),
      pvpStatusCtrl: new FormControl(null),
      legalityCtrl: new FormControl(null),
      groupStatusCtrl: new FormControl(null),
    })
  }

  public patchFromExisting(existing: CustomNotificationViewModel) {
    forkJoin({
      serverRegion: this.lookup.getServerRegions(),
      gameEnvironment: this.lookup.getGameEnvironments(),
      gameExperience: this.lookup.getGameExperiences(),
      category: this.lookup.getGameplayCategories(),
      subcategory: this.lookup.getGameplaySubcategories(),
      system: this.lookup.getPlanetarySystems(),
      language: LANGUAGE_OPTIONS,
      pvpStatus: this.lookup.getPvpStatuses(),
      legalities: this.lookup.getLegalities(),
      groupStatus: this.lookup.getGroupStatuses(),
    }).subscribe( data => {
      this.customNotificationForm.patchValue({
        labelCtrl: existing.tagLabel,
        serverCtrl: existing.serverId,
        environmentCtrl: existing.environmentId,
        experienceCtrl: existing.experienceId,
        categoryCtrl: existing.categoryId,
        subcategoryCtrl: existing.subcategoryId,
        systemCtrl: existing.systemId,
        languageCtrl: existing.languageCode,
        pvpStatusCtrl: existing.pvpStatusId,
        legalityCtrl: existing.legalityId,
        groupStatusCtrl: existing.groupStatusId
      })
    })
  }

  get labelCtrl(): FormControl { return this.customNotificationForm.get('labelCtrl') as FormControl }
  get serverCtrl(): FormControl { return this.customNotificationForm.get('serverCtrl') as FormControl }
  get environmentCtrl(): FormControl { return this.customNotificationForm.get('environmentCtrl') as FormControl }
  get experienceCtrl(): FormControl { return this.customNotificationForm.get('experienceCtrl') as FormControl }
  get categoryCtrl(): FormControl { return this.customNotificationForm.get('categoryCtrl') as FormControl }
  get subcategoryCtrl(): FormControl { return this.customNotificationForm.get('subcategoryCtrl') as FormControl }
  get systemCtrl(): FormControl { return this.customNotificationForm.get('systemCtrl') as FormControl }
  get languageCtrl(): FormControl { return this.customNotificationForm.get('languageCtrl') as FormControl }
  get pvpStatusCtrl(): FormControl { return this.customNotificationForm.get('pvpStatusCtrl') as FormControl }
  get legalityCtrl(): FormControl { return this.customNotificationForm.get('legalityCtrl') as FormControl }
  get groupStatusCtrl(): FormControl { return this.customNotificationForm.get('groupStatusCtrl') as FormControl }

}
