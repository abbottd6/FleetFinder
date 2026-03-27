import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CreateListingComponent } from './create-listing.component';
import { UserListingManagementService } from '../../services/user-services/user-listing-management.service';
import { Router } from '@angular/router';
import { ListingFormService } from '../../services/listing-form-service/listing-form.service';

describe('CreateListingComponent', () => {
  let component: CreateListingComponent;
  let fixture: ComponentFixture<CreateListingComponent>;

  beforeEach(async () => {
    const userListingSpy = jasmine.createSpyObj('UserListingManagementService', ['createListing', 'updateListing']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate', 'navigateByUrl']);
    const formServiceSpy = jasmine.createSpyObj('ListingFormService', ['patchFromDraft', 'resetForm'], {
      listingFormGroup: new FormGroup({})
    });

    await TestBed.configureTestingModule({
      declarations: [CreateListingComponent],
      providers: [
        { provide: UserListingManagementService, useValue: userListingSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ListingFormService, useValue: formServiceSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CreateListingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('listingForm should be initialized from formService', () => {
    expect(component.listingForm).toBeDefined();
  });
});
