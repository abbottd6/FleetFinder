import {AfterViewInit, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {ListingFormService, ListingFormShape} from "../../services/listing-form-service/listing-form.service";
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {InputFieldModule} from "../input-fields/input-field/input-field.module";
import {DropdownModule} from "../dropdowns/dropdown-module/dropdown.module";
import {MatError} from "@angular/material/form-field";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {environment} from "../../../environments/environment";
import {UserListingService} from "../../services/group-listing-services/user-listing.service";
import {NgIf} from "@angular/common";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {LookupService} from "../../services/api-lookup-services/lookup.service";
import {forkJoin} from "rxjs";

@Component({
  selector: 'app-update-listing',
  standalone: true,
  templateUrl: './update-listing.component.html',
  styleUrl: '../create-listing/create-listing.component.css',
  imports: [
    ReactiveFormsModule,
    InputFieldModule,
    DropdownModule,
    MatError,
    NgIf
  ]
})
export class UpdateListingComponent implements OnInit {
  public formSubmitted: boolean = false;
  listingForm!: FormGroup<ListingFormShape>;

  constructor(private userListingService: UserListingService, private router: Router,
              public formService: ListingFormService) {
  }

  ngOnInit() {
    this.listingForm = this.formService.listingFormGroup;

    const draft = history.state?.draft as GroupListingViewModel | undefined;

    if (draft){
      this.formService.patchFromDraft(draft);
    }
  }

  onSubmit() {
    if (this.listingForm.invalid) {
      this.listingForm.markAllAsTouched();
      this.formSubmitted = true;
      return;
    }

    const newListingData = new CreateListingRequest(this.listingForm.value);

    if(!environment.production) {
      console.log(newListingData);
    }

    this.userListingService.createListing(newListingData).subscribe({
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
    this.listingForm.reset();
    this.formSubmitted = false;

    this.router.navigateByUrl("/group-listings")
  }
}
