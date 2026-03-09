import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';

import { ListingOwnerActionsService } from './listing-owner-actions.service';
import { UserListingManagementService } from '../../user-services/user-listing-management.service';
import { UserService } from '../../user-services/user.service';
import { ListingTemplatesApiService } from '../../api-services/listing-templates-api/listing-templates-api.service';
import { ListingViewInteractionsService } from './listing-view-interactions.service';

describe('ListingOwnerActionsService', () => {
  let service: ListingOwnerActionsService;
  let userListingSpy: jasmine.SpyObj<UserListingManagementService>;
  let userSpy: jasmine.SpyObj<UserService>;
  let templatesApiSpy: jasmine.SpyObj<ListingTemplatesApiService>;
  let listingInteractSpy: jasmine.SpyObj<ListingViewInteractionsService>;
  let snackBarSpy: jasmine.SpyObj<MatSnackBar>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(() => {
    userListingSpy = jasmine.createSpyObj('UserListingManagementService', [
      'createListing', 'deleteListing', 'updateListing',
    ]);

    userSpy = jasmine.createSpyObj('UserService', ['refreshUser'], {
      'sessionUser': null,
    });

    templatesApiSpy = jasmine.createSpyObj('ListingTemplatesApiService', [
      'getTemplates', 'createTemplate', 'deleteTemplate',
    ]);

    listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService', ['emitRefresh', 'setSelectedListing']);

    snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate', 'navigateByUrl']);

    TestBed.configureTestingModule({
      providers: [
        ListingOwnerActionsService,
        { provide: UserListingManagementService, useValue: userListingSpy },
        { provide: UserService, useValue: userSpy },
        { provide: ListingTemplatesApiService, useValue: templatesApiSpy },
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: MatDialog, useValue: dialogSpy },
        { provide: Router, useValue: routerSpy },
      ]
    });

    service = TestBed.inject(ListingOwnerActionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('userUpdateSelected() navigates to /update-listing with state', () => {
    const mockListing = { groupId: 1, listingTitle: 'Test' } as any;
    service.userUpdateSelected(mockListing);
    expect(routerSpy.navigate).toHaveBeenCalledWith(
      ['/update-listing'],
      jasmine.objectContaining({ state: { draft: mockListing } })
    );
  });
});
