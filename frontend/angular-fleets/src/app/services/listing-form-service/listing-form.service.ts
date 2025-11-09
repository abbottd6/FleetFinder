import {Injectable, Input, OnDestroy, OnInit} from '@angular/core';
import {FormControl, FormGroup, NonNullableFormBuilder, Validators} from "@angular/forms";
import {requiredIfGroupStatusFuture} from "../../common/validators/custom-validators";
import {catchError, forkJoin, of, Subscription} from "rxjs";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {LookupService} from "../api-lookup-services/lookup.service";
import {environment} from "../../../environments/environment";

type TitleGroup = {
  listingTitle: FormControl<string>;
};
type SessionEnvInfoGroup = {
  serverRegion: FormControl<any>;
  gameEnvironment: FormControl<any>;
  gameExperience: FormControl<any>;
};
type GameplayInfoGroup = {
  playStyle: FormControl<any>;
  category: FormControl<any>;
  subcategory: FormControl<any>;
  legality: FormControl<any>;
  pvpStatus: FormControl<any>;
  planetarySystem: FormControl<any>;
  planetMoon: FormControl<any>;
  listingDescription: FormControl<string>;
};
type GroupSpecInfoGroup = {
  groupStatus: FormControl<any>;
  eventScheduleDate: FormControl<string | null>;
  eventScheduleTime: FormControl<string | null>;
  eventScheduleZone: FormControl<string | null>;
  currentPartySize: FormControl<number | null>;
  desiredPartySize: FormControl<number | null>;
  availableRoles: FormControl<string | null>;
  commsOption: FormControl<any>;
  commsService: FormControl<any>;
};

export type ListingFormShape = {
  titleGroup: FormGroup<TitleGroup>;
  sessionEnvInfoGroup: FormGroup<SessionEnvInfoGroup>;
  gameplayInfoGroup: FormGroup<GameplayInfoGroup>;
  groupSpecInfoGroup: FormGroup<GroupSpecInfoGroup>;
}

@Injectable({
  providedIn: 'root'
})
export class ListingFormService implements OnDestroy{
  private subs = new Subscription();
  listingFormGroup!: FormGroup<ListingFormShape>;

  constructor(private formBuilder: NonNullableFormBuilder, private lookup: LookupService) {
    this.listingFormGroup = this.buildForm();
    this.initSubscriptions();
  }

  ngOnDestroy() {
    this.subs.unsubscribe();
  }

  private initSubscriptions(): void {
    //Updating eventScheduleDate, eventScheduleTime, eventScheduleZone error status in relation to groupStatus
    const group_status = this.groupStatus?.valueChanges.subscribe(() => {
      this.eventScheduleDate?.updateValueAndValidity();
      this.eventScheduleTime?.updateValueAndValidity();
      this.eventScheduleZone?.updateValueAndValidity();
    })
    this.subs.add(group_status);
  }

  //method for checking whether event schedule fields are valid
  //event date, time, and time zone are only required if group status is "future/scheduled"
  //method is for displaying a single error if any of the three fields are invalid
  get isEventScheduleInvalid(): boolean {
    const dateError = this.eventScheduleDate?.hasError('required')
      && (this.eventScheduleDate.dirty || this.eventScheduleDate.touched);

    const timeError = this.eventScheduleTime?.hasError('required')
      && (this.eventScheduleTime.dirty || this.eventScheduleTime.touched);

    const zoneError = this.eventScheduleZone?.hasError('required')
      && (this.eventScheduleZone.dirty || this.eventScheduleZone.touched);

    return dateError || timeError || zoneError;
  }

  private buildForm(): FormGroup<ListingFormShape> {
    return this.formBuilder.group<ListingFormShape>({
      titleGroup: this.formBuilder.group<TitleGroup>({
        listingTitle: this.formBuilder.control('', [Validators.required, Validators.minLength(5)]),
      }),
      sessionEnvInfoGroup: this.formBuilder.group<SessionEnvInfoGroup>({
        serverRegion: new FormControl(null, [Validators.required]),
        gameEnvironment: new FormControl(null, [Validators.required]),
        gameExperience: new FormControl(null, [Validators.required]),
      }),
      gameplayInfoGroup: this.formBuilder.group<GameplayInfoGroup>({
        playStyle: new FormControl(null),
        category: new FormControl(null, [Validators.required]),
        subcategory: new FormControl({value: null, disabled: true}),
        legality: new FormControl(null, [Validators.required]),
        pvpStatus: new FormControl(null, [Validators.required]),
        planetarySystem: new FormControl(null, [Validators.required]),
        planetMoon: new FormControl({value: null, disabled: true}),
        listingDescription: this.formBuilder.control('', [Validators.required, Validators.minLength(15)]),
      }),
      groupSpecInfoGroup: this.formBuilder.group<GroupSpecInfoGroup>({
        groupStatus: new FormControl(null, [Validators.required]),
        eventScheduleDate: new FormControl({value: null, disabled: true}, [requiredIfGroupStatusFuture]),
        eventScheduleTime: new FormControl({value: null, disabled: true}, [requiredIfGroupStatusFuture]),
        eventScheduleZone: new FormControl({value: null, disabled: true}, [requiredIfGroupStatusFuture]),
        currentPartySize: new FormControl(null, [Validators.required]),
        desiredPartySize: new FormControl(null, [Validators.required]),
        availableRoles: new FormControl(null, [Validators.minLength(3)]),
        commsOption: new FormControl(null, [Validators.required]),
        commsService: new FormControl({value: null, disabled: true}),
      })
    });
  }

  public patchFromDraft(draft: GroupListingViewModel) {
    forkJoin({
      serverRegion: this.lookup.getServerRegions(),
      gameEnvironment: this.lookup.getGameEnvironments(),
      gameExperience: this.lookup.getGameExperiences(),
      playStyle: this.lookup.getPlayStyles(),
      legalities: this.lookup.getLegalities(),
      groupStatuses: this.lookup.getGroupStatuses(),
      category: this.lookup.getGameplayCategories(),
      subcategory: this.lookup.getGameplaySubcategories(),
      pvpStatus: this.lookup.getPvpStatuses(),
      planetarySystem: this.lookup.getPlanetarySystems(),
      planetMoon: this.lookup.getPlanetMoonSystems(),
      groupStatus: this.lookup.getGroupStatuses(),
    }).subscribe(data => {
      this.listingFormGroup.patchValue({
        titleGroup: {listingTitle: draft.listingTitle},
        sessionEnvInfoGroup: {
          serverRegion: draft.serverId,
          gameEnvironment: draft.environmentId,
          gameExperience: draft.experienceId,
        },
        gameplayInfoGroup: {
          playStyle: draft.styleId,
          legality: draft.legalityId,
          pvpStatus: draft.pvpStatusId,
          // category, subcategory, system, and planet are patched with setValue (below) so that child dropdowns filter
          // and update based on the patched value.
          listingDescription: draft.listingDescription,
        },
        groupSpecInfoGroup: {
          groupStatus: draft.groupStatusId,
          currentPartySize: draft.currentPartySize,
          desiredPartySize: draft.desiredPartySize,
          availableRoles: draft.availableRoles,
          commsOption: draft.commsOption,
          commsService: draft.commsService
        }
      });
      this.category?.setValue(draft.categoryId);
      this.subcategoryControl.setValue(draft.subcategoryId);
      this.planetarySystem?.setValue(draft.systemId);
      this.planetMoon?.setValue(draft.planetId);
      this.reverseParseAndPatchDateString(draft.eventSchedule);
    });
  }

  reverseParseAndPatchDateString(eventSchedule: Date) {
    if (eventSchedule != null) {
      const utc = new Date(eventSchedule);

      const year = utc.getFullYear();
      const month = utc.getMonth() + 1;
      const day = utc.getDate();
      const hours = utc.getHours();
      const minutes = utc.getMinutes();

      const parsedDate = new Date(`${this.padDateString(month)}/${this.padDateString(day)}/${year}`);
      console.log("Parsed date: ", parsedDate)

      const parsedTime = `${this.padDateString(hours)}:${this.padDateString(minutes)}`;
      console.log("Parsed time: ", parsedTime);

      this.eventScheduleDate?.setValue(parsedDate);
      this.eventScheduleTime?.setValue(parsedTime);

      [this.eventScheduleDate, this.eventScheduleTime, this.eventScheduleZone].forEach(ctrl => {
        ctrl?.updateValueAndValidity({onlySelf: true, emitEvent: false});
        console.log('value:', ctrl?.value, 'errors:', ctrl?.errors);
        ctrl?.markAsPristine();
        ctrl?.markAsUntouched();
      });
    }
  }

  private padDateString(value: number): string {
    return value.toString().padStart(2, '0');
  }

  //Getters for passing FormControl entities to child components
  //titleGroup
  get listingTitle(): FormControl { return this.listingFormGroup.get('titleGroup.listingTitle') as FormControl}

  //sessionEnvInfoGroup
  get serverRegion(): FormControl { return this.listingFormGroup.get('sessionEnvInfoGroup.serverRegion') as FormControl}
  get gameEnvironment(): FormControl { return this.listingFormGroup.get('sessionEnvInfoGroup.gameEnvironment') as FormControl}
  get gameExperience(): FormControl { return this.listingFormGroup.get('sessionEnvInfoGroup.gameExperience') as FormControl}

  //gameplayInfoGroup
  get playStyle(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.playStyle') as FormControl }
  get category(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.category') as FormControl }
  get subcategoryControl(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.subcategory') as FormControl }
  get legality(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.legality') as FormControl }
  get pvpStatus(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.pvpStatus') as FormControl }
  get planetarySystem(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.planetarySystem') as FormControl }
  get planetMoon(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.planetMoon') as FormControl }
  get listingDescription(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.listingDescription') as FormControl }

  //groupSpecInfoGroup
  get groupStatus(): FormControl { return this.listingFormGroup.get('groupSpecInfoGroup.groupStatus') as FormControl }
  get eventScheduleDate(): FormControl { return this.listingFormGroup.get('groupSpecInfoGroup.eventScheduleDate') as FormControl }
  get eventScheduleTime(): FormControl { return this.listingFormGroup.get('groupSpecInfoGroup.eventScheduleTime') as FormControl }
  get eventScheduleZone(): FormControl { return this.listingFormGroup.get('groupSpecInfoGroup.eventScheduleZone') as FormControl }
  get currentPartySize(): FormControl { return this.listingFormGroup.get('groupSpecInfoGroup.currentPartySize') as FormControl }
  get desiredPartySize(): FormControl  { return this.listingFormGroup.get('groupSpecInfoGroup.desiredPartySize') as FormControl }
  get availableRoles(): FormControl { return this.listingFormGroup.get('groupSpecInfoGroup.availableRoles') as FormControl }
  get commsOption(): FormControl { return this.listingFormGroup.get('groupSpecInfoGroup.commsOption') as FormControl }
  get commsService(): FormControl { return this.listingFormGroup.get('groupSpecInfoGroup.commsService') as FormControl }
}
