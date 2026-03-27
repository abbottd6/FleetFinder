import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { MessageComponent } from './message.component';
import { UserService } from '../../../services/user-services/user.service';

describe('MessageComponent', () => {
  let component: MessageComponent;
  let fixture: ComponentFixture<MessageComponent>;

  beforeEach(async () => {
    const userServiceSpy = jasmine.createSpyObj('UserService', [], { userId: 42 });

    await TestBed.configureTestingModule({
      imports: [MessageComponent],
      providers: [
        { provide: UserService, useValue: userServiceSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(MessageComponent);
    component = fixture.componentInstance;
    component.msg = { senderId: 42 } as any;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('userIsSender returns true when senderId matches userId', () => {
    component.msg = { senderId: 42 } as any;
    expect(component.userIsSender).toBeTrue();
  });

  it('userIsSender returns false when senderId differs', () => {
    component.msg = { senderId: 99 } as any;
    expect(component.userIsSender).toBeFalse();
  });
});
