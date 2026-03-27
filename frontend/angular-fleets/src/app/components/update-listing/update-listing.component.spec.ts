import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { UpdateListingComponent } from './update-listing.component';
import { UserListingManagementService } from '../../services/user-services/user-listing-management.service';
import { ListingFormService } from '../../services/listing-form-service/listing-form.service';

function buildMockFormGroup(): FormGroup {
  const fb = new FormBuilder();
  return fb.group({
    titleGroup: fb.group({ listingTitle: [''] }),
    sessionEnvInfoGroup: fb.group({ serverRegion: [null], gameEnvironment: [null], gameExperience: [null] }),
    gameplayInfoGroup: fb.group({ playStyle: [null], category: [null], subcategory: [null], legality: [null], pvpStatus: [null], planetarySystem: [null], planetMoon: [null], listingDescription: [''] }),
    groupSpecInfoGroup: fb.group({ groupStatus: [null], eventScheduleDate: [null], eventScheduleTime: [null], eventScheduleZone: [null], currentPartySize: [null], desiredPartySize: [null], availableRoles: [null], commsOption: [null], commsService: [null], language: [null] })
  });
}

describe('UpdateListingComponent', () => {
  let component: UpdateListingComponent;
  let fixture: ComponentFixture<UpdateListingComponent>;

  beforeEach(async () => {
    const userListingSpy = jasmine.createSpyObj('UserListingManagementService', ['updateListing']);
    const mockGroup = buildMockFormGroup();
    const formServiceSpy = jasmine.createSpyObj('ListingFormService', ['patchFromDraft', 'resetForm'], {
      listingFormGroup: mockGroup,
      listingTitle: new FormControl(''),
      serverRegion: new FormControl(null),
      gameEnvironment: new FormControl(null),
      gameExperience: new FormControl(null),
      playStyle: new FormControl(null),
      category: new FormControl(null),
      subcategoryControl: new FormControl(null),
      legality: new FormControl(null),
      pvpStatus: new FormControl(null),
      planetarySystem: new FormControl(null),
      planetMoon: new FormControl(null),
      listingDescription: new FormControl(''),
      groupStatus: new FormControl(null),
      eventScheduleDate: new FormControl(null),
      eventScheduleTime: new FormControl(null),
      eventScheduleZone: new FormControl(null),
      currentPartySize: new FormControl(null),
      desiredPartySize: new FormControl(null),
      availableRoles: new FormControl(null),
      commsOption: new FormControl(null),
      commsService: new FormControl(null),
      language: new FormControl(null),
    });

    await TestBed.configureTestingModule({
      imports: [UpdateListingComponent, HttpClientTestingModule, RouterModule.forRoot([])],
      providers: [
        { provide: UserListingManagementService, useValue: userListingSpy },
        { provide: ListingFormService, useValue: formServiceSpy },
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateListingComponent);
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
