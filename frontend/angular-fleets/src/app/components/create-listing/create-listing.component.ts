import {Component, OnInit} from '@angular/core';
import {DropdownModule} from '../dropdowns/dropdown-module/dropdown.module';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {CreateListingService} from "../../services/group-listing-services/create-listing.service";
import {Router} from "@angular/router";

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
      }),
      descriptionGroup: this.formBuilder.group({
        listingDescription: [null, [Validators.required, Validators.minLength(15)]],
      }),
      groupSpecInfoGroup: this.formBuilder.group({
        groupStatus: [null, Validators.required],
        eventScheduleDate: [{value: null, disabled: true}],
        eventScheduleTime: [{value: null, disabled: true}],
        eventScheduleZone: [{value: null, disabled: true}],
        currentPartySize: [null, [Validators.required]],
        desiredPartySize: [null, [Validators.required]],
        availableRoles: [null, [Validators.minLength(3)]],
        commsOption: [null, [Validators.required]],
        commsService: [{value: null, disabled: true}],
      })
    })
  }

  onSubmit() {
    if (this.listingFormGroup.invalid) {
      this.listingFormGroup.markAllAsTouched();
      this.formSubmitted = true;
      return;
    }

    const newListingData = new CreateListingRequest(3, this.listingFormGroup.value);

    console.log(newListingData);

    this.createListingService.createListing(newListingData).subscribe({
        next: response => {
          console.log(response.listingTitle)
          alert(`Your creation of group listing, ${response.listingTitle} was successful!`);

          this.resetAndRedirect();
        },
        error: err => {
          alert(`There was an error creating your listing: ${err.message}`);
        }
      }
    )
  }

  private resetAndRedirect() {
    this.listingFormGroup.reset();
    this.formSubmitted = false;

    this.router.navigateByUrl("/group-listings")
  }

  get serverRegion(): FormControl { return this.listingFormGroup.get('sessionEnvInfoGroup.serverRegion') as FormControl}
  get gameEnvironment(): FormControl { return this.listingFormGroup.get('sessionEnvInfoGroup.gameEnvironment') as FormControl}
  get gameExperience(): FormControl { return this.listingFormGroup.get('sessionEnvInfoGroup.gameExperience') as FormControl}

  //Getters for passing FormGroups to children
  get titleGroup(): FormGroup {
    return this.listingFormGroup.get('titleGroup') as FormGroup;
  }

  get sessionEnvInfoGroup(): FormGroup {
    return this.listingFormGroup.get('sessionEnvInfoGroup') as FormGroup;
  }

  get gameplayInfoGroup(): FormGroup {
    return this.listingFormGroup.get('gameplayInfoGroup') as FormGroup;
  }

  get groupSpecInfoGroup(): FormGroup {
    return this.listingFormGroup.get('groupSpecInfoGroup') as FormGroup;
  }

  get descriptionGroup(): FormGroup {
    return this.listingFormGroup.get('descriptionGroup') as FormGroup;
  }
}
