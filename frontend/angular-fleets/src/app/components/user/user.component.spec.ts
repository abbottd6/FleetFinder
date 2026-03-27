import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, NEVER } from 'rxjs';

import { UserComponent } from './user.component';
import { UserService } from '../../services/user-services/user.service';
import { AuthService } from '../../services/auth/auth-services/auth.service';
import { ListingViewInteractionsService } from '../../services/facade-services/listing-view-interactions/listing-view-interactions.service';
import { TemplatesModalService } from '../../services/component-services/templates-modal-service/templates-modal.service';
import { ChatHostService } from '../../services/facade-services/chat/chat-host.service';
import { UserApiService } from '../../services/user-services/userApi.service';
import { UpdateUserFormService } from '../../services/user-services/update-user-form.service';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';

describe('UserComponent', () => {
  let component: UserComponent;
  let fixture: ComponentFixture<UserComponent>;

  beforeEach(async () => {
    const userSpy = jasmine.createSpyObj('UserService', ['refreshUser'], {
      sessionUser$: of(null),
      userId: 0
    });
    const authSpy = jasmine.createSpyObj('AuthService', ['logout'], {
      isLoggedIn$: of(false)
    });
    const listingInteractSpy = jasmine.createSpyObj('ListingViewInteractionsService',
      ['setSelectedListing'], {
        selectedListing$: of(null),
        refresh$: NEVER,
        bookmarkedIds$: of(new Set())
      });
    const templatesModalSpy = jasmine.createSpyObj('TemplatesModalService', ['openModal'], {
      refresh$: NEVER
    });
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['openChatWith', 'toggleChat'], {
      open$: of(false)
    });
    const userApiSpy = jasmine.createSpyObj('UserApiService', ['deleteUser', 'updateMe']);
    const userFormSrvSpy = jasmine.createSpyObj('UpdateUserFormService', [], {
      serverControl: { value: null },
      orgControl: { value: null }
    });
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [UserComponent],
      providers: [
        { provide: UserService, useValue: userSpy },
        { provide: AuthService, useValue: authSpy },
        { provide: ListingViewInteractionsService, useValue: listingInteractSpy },
        { provide: TemplatesModalService, useValue: templatesModalSpy },
        { provide: ChatHostService, useValue: chatHostSpy },
        { provide: UserApiService, useValue: userApiSpy },
        { provide: UpdateUserFormService, useValue: userFormSrvSpy },
        { provide: Router, useValue: routerSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(UserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('selectedTab starts at listings', () => {
    expect(component.selectedTab).toBe('listings');
  });
});
