import {Component, OnInit} from '@angular/core';
import {DropdownModule} from '../dropdowns/dropdown-module/dropdown.module';
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-create-listing',
  templateUrl: './create-listing.component.html',
  styleUrl: './create-listing.component.css',
  standalone: false
})
export class CreateListingComponent  implements OnInit {
  listingFormGroup: FormGroup = new FormGroup({});

  constructor(private formBuilder: FormBuilder) {}

  ngOnInit() {

    this.listingFormGroup = this.formBuilder.group({
      titleGroup: this.formBuilder.group({
        listingTitle: [null, [Validators.required]],
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
        listingDescription: [null, [Validators.required]],
      }),
      groupSpecInfoGroup: this.formBuilder.group({
        groupStatus: [null, Validators.required],
        eventScheduleDate: [{value: null, disabled: true}],
        currentPartySize: [null, [Validators.required]],
        desiredPartySize: [null, [Validators.required]],
        availableRoles: [null],
        commsOption: [null, [Validators.required]],
        commsService: [{value: null, disabled: true}],
      })
    })
  }

  onSubmit() {
    const formData = this.listingFormGroup.value;

    console.log("Handling the submit button");
    console.log(formData);

    const payload = {
      userId: 3,
      listingTitle: formData.titleGroup.listingTitle,
      serverId: formData.sessionEnvInfoGroup.serverRegion,
      environmentId: formData.sessionEnvInfoGroup.gameEnvironment,
      experienceId: formData.sessionEnvInfoGroup.gameExperience,
      playStyleId: formData.gameplayInfoGroup.playStyle,
      categoryId: formData.gameplayInfoGroup.category.gameplayCategoryId,
      subcategoryId: formData.gameplayInfoGroup.subcategory,
      legalityId: formData.gameplayInfoGroup.legality,
      pvpStatusId: formData.gameplayInfoGroup.pvpStatus,
      systemId: formData.gameplayInfoGroup.planetarySystem.systemId,
      planetId: formData.gameplayInfoGroup.planetMoon,
      listingDescription: formData.gameplayInfoGroup.listingDescription,
      groupStatusId: formData.groupSpecInfoGroup.groupStatus,
      eventScheduleDate: formData.groupSpecInfoGroup.eventScheduleDate,
      currentPartySize: formData.groupSpecInfoGroup.currentPartySize,
      desiredPartySize: formData.groupSpecInfoGroup.desiredPartySize,
      availableRoles: formData.groupSpecInfoGroup.availableRoles,
      commsOption: formData.groupSpecInfoGroup.commsOption.option,
      commsService: formData.groupSpecInfoGroup.commsService,
    };

    console.log(payload);
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
}
