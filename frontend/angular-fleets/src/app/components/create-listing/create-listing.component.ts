import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-create-listing',
  templateUrl: './create-listing.component.html',
  styleUrl: './create-listing.component.css',
})
export class CreateListingComponent implements OnInit{

  //this was throwing an error, need to check if this should be undefined (suggested)
  //or if there is a different problem.
  createListingFormGroup: FormGroup | undefined;

  constructor(private formBuilder: FormBuilder) {

  }
  ngOnInit(): void {

    this.createListingFormGroup = this.formBuilder.group({
      groupListing: this.formBuilder.group({
        serverId: [null, Validators.required],
        environmentId: [null, Validators.required],
        experienceId: [null, Validators.required],
        listingTitle: ['', Validators.required, Validators.maxLength(65)],
        styleId: [null, Validators.required],
        legalityId: [null, Validators.required],
        groupStatusId: [null, Validators.required],
        eventSchedule: [{value: null, disabled: true}],
        categoryId: [null, Validators.required],
        subcategoryId: [null],
        pvpStatusId: [null, Validators.required],
        systemId: [null, Validators.required],
        planetId: [null],
        listingDescription: ['', Validators.required, Validators.maxLength(500)],
        desiredPartySize: ['', Validators.required, Validators.min(2), Validators.max(1000)],
        currentPartySize: ['', Validators.required, Validators.min(1), Validators.max(1000)],
        availableRoles: ['', Validators.required, Validators.maxLength(100)],
        commsOption: [null, Validators.required],
        commsService: ['', Validators.maxLength(25)],
      })
    });
  }

}
