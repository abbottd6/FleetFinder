import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, NEVER } from 'rxjs';
import { Router } from '@angular/router';

import { UserProfileTemplatesComponent } from './user-profile-templates.component';
import { ListingTemplatesApiService } from '../../services/api-services/listing-templates-api/listing-templates-api.service';
import { ListingViewInteractionsService } from '../../services/facade-services/listing-view-interactions/listing-view-interactions.service';
import { TemplatesModalService } from '../../services/component-services/templates-modal-service/templates-modal.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';

describe('UserProfileTemplatesComponent', () => {
  let component: UserProfileTemplatesComponent;
  let fixture: ComponentFixture<UserProfileTemplatesComponent>;

  beforeEach(async () => {
    const templatesApiSpy = jasmine.createSpyObj('ListingTemplatesApiService', ['getTemplates', 'deleteTemplate']);
    templatesApiSpy.getTemplates.and.returnValue(of({
      content: [],
      page: { totalElements: 0, size: 10, number: 0, totalPages: 0 }
    }));

    const listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService',
      ['setSelectedListing'], { selectedListing$: of(null), refresh$: NEVER });
    const templatesModalSpy = jasmine.createSpyObj('TemplatesModalService',
      ['openModal'], { refresh$: NEVER });
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const snackBarSpy = jasmine.createSpyObj('MatSnackBar', ['open']);
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [UserProfileTemplatesComponent],
      providers: [
        { provide: ListingTemplatesApiService, useValue: templatesApiSpy },
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
        { provide: TemplatesModalService, useValue: templatesModalSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatSnackBar, useValue: snackBarSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(UserProfileTemplatesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
