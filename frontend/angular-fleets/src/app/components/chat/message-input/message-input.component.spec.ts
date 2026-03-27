import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { MessageInputComponent } from './message-input.component';
import { ChatHostService } from '../../../services/facade-services/chat/chat-host.service';

describe('MessageInputComponent', () => {
  let component: MessageInputComponent;
  let fixture: ComponentFixture<MessageInputComponent>;

  beforeEach(async () => {
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['sendMessage'], {
      windowState$: of('NORMAL'),
      open$: of(true)
    });

    await TestBed.configureTestingModule({
      imports: [MessageInputComponent],
      providers: [
        { provide: ChatHostService, useValue: chatHostSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(MessageInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have an inputCtrl form control', () => {
    expect(component.inputCtrl).toBeDefined();
  });
});
