import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { NotificationComponent } from './notification.component';
import { NotificationService } from '../../../../services/facade-services/notifications/notification.service';

describe('NotificationComponent', () => {
  let component: NotificationComponent;
  let fixture: ComponentFixture<NotificationComponent>;

  beforeEach(async () => {
    const noteSpy = jasmine.createSpyObj('NotificationService', ['deleteNotification'], {
      openState$: of(false),
      closingIds: new Set<number>()
    });

    await TestBed.configureTestingModule({
      imports: [NotificationComponent],
      providers: [
        { provide: NotificationService, useValue: noteSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationComponent);
    component = fixture.componentInstance;
    component.note = { notificationId: 1, type: 'LISTING_ARCHIVED', title: 'Test Title', message: 'Test message', createdAt: new Date() } as any;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
