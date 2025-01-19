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

  constructor(private formBuilder: FormBuilder, private createListingService: CreateListingService,
              private router: Router) {}

  ngOnInit() {

    this.listingFormGroup = this.formBuilder.group({
      titleGroup: this.formBuilder.group({
        listingTitle: [null, [Validators.required, Validators.minLength(3)]],
      }),
      sessionEnvInfoGroup: this.formBuilder.group({
        serverRegion: [null, Validators.required],
        gameEnvironment: [null, Validators.required],
        gameExperience: [null, Validators.required],
      }),
      gameplayInfoGroup: this.formBuilder.group({
        playStyle: [null],
        category: [null, [Validators.required]],
        subcategory: [{value: null, disabled: true}],
        legality: [null, [Validators.required]],
        pvpStatus: [null, [Validators.required]],
        planetarySystem: [null, [Validators.required]],
        planetMoon: [{value: null, disabled: true}],
        listingDescription: [null, [Validators.required, Validators.minLength(3)]],
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

    this.router.navigateByUrl("/group-listings")
  }

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

  //Field getters for form validation
  get listingTitle() { return this.listingFormGroup.get('titleGroup.listingTitle'); }

  get serverRegion() {return this.listingFormGroup.get('sessionEnvInfoGroup.serverRegion')}
  get gameEnvironment() { return this.listingFormGroup.get('sessionEnvInfoGroup.gameEnvironment')}
  get gameExperience() { return this.listingFormGroup.get('sessionEnvInfoGroup.gameExperience')}

  get playStyle() { return this.listingFormGroup.get('gameplayInfoGroup.playStyle')}
  get category() { return this.listingFormGroup.get('gameplayInfoGroup.category')}
  get subcategory() { return this.listingFormGroup.get('gameplayInfoGroup.subcategory')}
  get legality() { return this.listingFormGroup.get('gameplayInfoGroup.legality')}
  get pvpStatus() { return this.listingFormGroup.get('gameplayInfoGroup.pvpStatus')}
  get planetarySystem() { return this.listingFormGroup.get('gameplayInfoGroup.planetarySystem')}
  get listingDescription() { return this.listingFormGroup.get('gameplayInfoGroup.listingDescription')}

  get groupStatus() { return this.listingFormGroup.get('groupSpecInfoGroup.groupStatus')}
  get eventScheduleDate() { return this.listingFormGroup.get('groupSpecInfoGroup.eventScheduleDate')}
  get eventScheduleTime() { return this.listingFormGroup.get('groupSpecInfoGroup.eventScheduleTime')}
  get eventScheduleZone() { return this.listingFormGroup.get('groupSpecInfoGroup.eventScheduleZone')}
  get currentPartySize() { return this.listingFormGroup.get('groupSpecInfoGroup.currentPartySize')}
  get desiredPartySize() { return this.listingFormGroup.get('groupSpecInfoGroup.desiredPartySize')}
  get availableRoles() { return this.listingFormGroup.get('groupSpecInfoGroup.availableRoles')}
  get commsOption() { return this.listingFormGroup.get('groupSpecInfoGroup.commsOption')}
  get commsService() { return this.listingFormGroup.get('groupSpecInfoGroup.commsService')}

}
