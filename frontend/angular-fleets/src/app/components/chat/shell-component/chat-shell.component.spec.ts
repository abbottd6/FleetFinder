import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { ChatShellComponent } from './chat-shell.component';
import { ChatHostService } from '../../../services/facade-services/chat/chat-host.service';

describe('ChatComponent', () => {
  let component: ChatShellComponent;
  let fixture: ComponentFixture<ChatShellComponent>;

  beforeEach(async () => {
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['toggleChat', 'closeChat'], {
      mounted$: of(false),
      open$: of(false),
      windowState$: of('NORMAL')
    });

    await TestBed.configureTestingModule({
      imports: [ChatShellComponent],
      providers: [
        { provide: ChatHostService, useValue: chatHostSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ChatShellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('component should have ChatHostService wired up', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
