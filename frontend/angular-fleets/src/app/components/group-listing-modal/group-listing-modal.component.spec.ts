import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { GroupListingModalComponent } from './group-listing-modal.component';
import { UserService } from '../../services/user-services/user.service';
import { ChatHostService } from '../../services/facade-services/chat/chat-host.service';

describe('GroupListingModalComponent', () => {
  let component: GroupListingModalComponent;
  let fixture: ComponentFixture<GroupListingModalComponent>;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', [], {
      sessionUser$: of(null),
      userId: 0
    });
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['openChatWith', 'toggleChat'], {
      open$: of(false)
    });

    await TestBed.configureTestingModule({
      imports: [GroupListingModalComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: ChatHostService, useValue: chatHostSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(GroupListingModalComponent);
    component = fixture.componentInstance;
    component.isVisible = false;
    component.selectedListing = null;
    component.isBookmarked$ = of(false);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('close EventEmitter should be defined', () => {
    expect(component.close).toBeDefined();
  });
});
