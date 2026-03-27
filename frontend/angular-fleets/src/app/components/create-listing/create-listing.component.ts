import {Component, OnDestroy, OnInit} from '@angular/core';
import {Form, FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {CreateListingRequest} from "../../models/group-listing/create-listing-request";
import {UserListingManagementService} from "../../services/user-services/user-listing-management.service";
import {Router} from "@angular/router";
import {environment} from "../../../environments/environment";
import {ListingFormService, ListingFormShape} from "../../services/listing-form-service/listing-form.service";
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {ListingTemplateViewModel} from "../../models/listing-templates/listing-template-view-model";
import {BehaviorSubject, Subject} from "rxjs";
import {LANGUAGE_OPTIONS} from "../../models/language-options";

@Component({
  selector: 'app-create-listing',
  templateUrl: './create-listing.component.html',
  styleUrl: './create-listing.component.css',
  standalone: false
})
export class CreateListingComponent  implements OnInit, OnDestroy {
  private destroy$: Subject<void> = new Subject<void>();
  public formSubmitted: boolean = false;

  private submitSubject = new BehaviorSubject<boolean>(false);
  public submitting$ = this.submitSubject.asObservable();

  listingForm!: FormGroup<ListingFormShape>;

  draft!: GroupListingViewModel | ListingTemplateViewModel | undefined;

  listingData?: GroupListingViewModel;

  constructor(private userListingService: UserListingManagementService, private router: Router,
              public formService: ListingFormService) {}

  ngOnInit() {
    this.listingForm = this.formService.listingFormGroup;

    this.draft = history.state?.draft as GroupListingViewModel | undefined;
    this.listingData = this.draft as GroupListingViewModel;

    if(!environment.production) {
      console.log(this.draft);
    }

    if (this.draft){
      this.formService.patchFromDraft(this.draft);
      this.draft = undefined;
    }
  }

  onSubmit() {
    this.submitSubject.next(true);
    setTimeout(() => this.submitSubject.next(false), 4000);
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
          this.resetAndRedirect();
        },
        error: err => {
          alert(`There was an error creating your listing (note that there is a limit of 5 active listings per user).`);
        }
      }
    )
  }

  //reset form after valid submit
  private resetAndRedirect() {
    this.listingForm.reset();
    this.formSubmitted = false;

    this.router.navigateByUrl("/listing-success")
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly LANGUAGE_OPTIONS = LANGUAGE_OPTIONS;
}
