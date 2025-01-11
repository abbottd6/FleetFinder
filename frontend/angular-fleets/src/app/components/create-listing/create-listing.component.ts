import {Component, OnInit} from '@angular/core';
import {DropdownModule} from '../dropdowns/dropdown-module/dropdown.module';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

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
        pvpStatus: [null, [Validators.required]],
        legality: [null, [Validators.required]],
        planetarySystem: [null, [Validators.required]],
        planetMoon: [null],
        listingDescription: [null, [Validators.required]],
      }),
      groupSpecInfoGroup: this.formBuilder.group({
        groupStatus: [null, Validators.required],
        eventScheduleDate: [null],
        currentPartySize: [null, [Validators.required]],
        desiredPartySize: [null, [Validators.required]],
        availableRoles: [null],
        commsOption: [null, [Validators.required]],
        commsService: [null],
      })
    })
  }
}
