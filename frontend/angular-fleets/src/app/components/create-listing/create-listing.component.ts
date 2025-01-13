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
        subcategory: [null],
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
        commsService: [null],
      })
    })

    this.groupSpecInfoGroup.get('groupStatus')?.valueChanges.subscribe(value => {
      if (value == 2) {
        this.groupSpecInfoGroup.get('eventScheduleDate')?.enable();
      }
      else {
        this.groupSpecInfoGroup.get('eventScheduleDate')?.disable();
      }
    })
/* THIS ENABLE IS NOT WORKING
    this.gameplayInfoGroup.get('planetarySystem')?.valueChanges.subscribe(value => {
      console.log('planetarySystem value: ', value);
      if (value == 1 || value == 2) {
        this.groupSpecInfoGroup.get('planetMoon')?.enable();
      }
      else {
        this.groupSpecInfoGroup.get('planetMoon')?.disable();
      }
    });
*/
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

  protected readonly FormControl = FormControl;
}
