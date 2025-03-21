import {Component, OnInit} from '@angular/core';
import { requiredIfGroupStatusFuture } from "../../common/validators/custom-validators";
import {Form, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {CreateListingService} from "../../services/group-listing-services/create-listing.service";
import {Router} from "@angular/router";
import {group} from "@angular/animations";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'app-create-listing',
  templateUrl: './create-listing.component.html',
  styleUrl: './create-listing.component.css',
  standalone: false
})
export class CreateListingComponent  implements OnInit {
  listingFormGroup: FormGroup = new FormGroup({});
  formSubmitted: boolean = false;

  constructor(private formBuilder: FormBuilder, private createListingService: CreateListingService,
              private router: Router) {}

  ngOnInit() {

    this.listingFormGroup = this.formBuilder.group({
      titleGroup: this.formBuilder.group({
        listingTitle: new FormControl(null, [Validators.required, Validators.minLength(5)]),
      }),
      sessionEnvInfoGroup: this.formBuilder.group({
        serverRegion: new FormControl(null, [Validators.required]),
        gameEnvironment: new FormControl (null, [Validators.required]),
        gameExperience: new FormControl (null, [Validators.required]),
      }),
      gameplayInfoGroup: this.formBuilder.group({
        playStyle: new FormControl (null),
        category: new FormControl (null, [Validators.required]),
        subcategory: new FormControl ({value: null, disabled: true}),
        legality: new FormControl (null, [Validators.required]),
        pvpStatus: new FormControl (null, [Validators.required]),
        planetarySystem: new FormControl (null, [Validators.required]),
        planetMoon: new FormControl ({value: null, disabled: true}),
        listingDescription: new FormControl (null, [Validators.required, Validators.minLength(15)]),
      }),
      groupSpecInfoGroup: this.formBuilder.group({
        groupStatus: new FormControl (null, [Validators.required]),
        eventScheduleDate: new FormControl ({value: null, disabled: true}, [requiredIfGroupStatusFuture]),
        eventScheduleTime: new FormControl ({value: null, disabled: true}, [requiredIfGroupStatusFuture]),
        eventScheduleZone: new FormControl ({value: null, disabled: true}, [requiredIfGroupStatusFuture]),
        currentPartySize: new FormControl (null, [Validators.required]),
        desiredPartySize: new FormControl (null, [Validators.required]),
        availableRoles: new FormControl (null, [Validators.minLength(3)]),
        commsOption: new FormControl (null, [Validators.required]),
        commsService: new FormControl ({value: null, disabled: true}),
      })
    });

    //Updating eventScheduleDate, eventScheduleTime, eventScheduleZone error status in relation to groupStatus
    this.groupStatus?.valueChanges.subscribe(() => {
      this.eventScheduleDate?.updateValueAndValidity();
      this.eventScheduleTime?.updateValueAndValidity();
      this.eventScheduleZone?.updateValueAndValidity();
    })
  }

  onSubmit() {
    if (this.listingFormGroup.invalid) {
      this.listingFormGroup.markAllAsTouched();
      this.formSubmitted = true;
      return;
    }

    const newListingData = new CreateListingRequest(1, this.listingFormGroup.value);

    if(!environment.production) {
      console.log(newListingData);
    }

    this.createListingService.createListing(newListingData).subscribe({
        next: response => {
          if(!environment.production) {
            console.log(response.listingTitle)
          }
          alert(`Your creation of group listing, ${response.listingTitle} was successful!`);

          this.resetAndRedirect();
        },
        error: err => {
          alert(`There was an error creating your listing: ${err.message}`);
        }
      }
    )
  }

  //reset form after valid submit
  private resetAndRedirect() {
    this.listingFormGroup.reset();
    this.formSubmitted = false;

    this.router.navigateByUrl("/group-listings")
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
  get subcategory(): FormControl { return this.listingFormGroup.get('gameplayInfoGroup.subcategory') as FormControl }
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
