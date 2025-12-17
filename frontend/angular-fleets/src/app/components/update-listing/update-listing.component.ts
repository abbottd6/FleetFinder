import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {ListingFormService, ListingFormShape} from "../../services/listing-form-service/listing-form.service";
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {InputFieldModule} from "../input-fields/input-field/input-field.module";
import {DropdownModule} from "../dropdowns/dropdown-module/dropdown.module";
import {MatError} from "@angular/material/form-field";
import {environment} from "../../../environments/environment";
import {UserListingManagementService} from "../../services/user-services/user-listing-management.service";
import {NgIf} from "@angular/common";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {UpdateListingRequest} from "../../models/group-listing/update-listing-request";
import {Subject, takeUntil} from "rxjs";

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
    NgIf,
    RouterLink
  ]
})
export class UpdateListingComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  public formSubmitted: boolean = false;
  listingForm!: FormGroup<ListingFormShape>;
  listingData!: GroupListingViewModel;

  constructor(private userListingService: UserListingManagementService, private router: Router,
              public formService: ListingFormService) {}

  ngOnInit() {
    this.listingForm = this.formService.listingFormGroup;

    const draft = history.state?.draft as GroupListingViewModel | undefined;
    this.listingData = draft as GroupListingViewModel;

    if (draft){
      this.formService.patchFromDraft(draft);
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  onSubmit() {
    if (this.listingForm.invalid) {
      this.listingForm.markAllAsTouched();
      this.formSubmitted = true;
      return;
    }

    const newListingData = new UpdateListingRequest(this.listingForm.value, this.listingData.groupId);

    if(!environment.production) {
      console.log("HERE IS THE SUBMITTED LISTING DATA: ", newListingData);
    }

    this.userListingService.updateListing(newListingData).subscribe({
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
