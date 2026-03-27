import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of } from 'rxjs';

import { NotificationsDropdownComponent } from './notifications-dropdown.component';
import { WsGatewayService } from '../../../../services/websocket-messaging/ws-gateway.service';
import { NotificationService } from '../../../../services/facade-services/notifications/notification.service';

describe('NotificationsDropdownComponent', () => {
  let component: NotificationsDropdownComponent;
  let fixture: ComponentFixture<NotificationsDropdownComponent>;

  beforeEach(async () => {
    const wsSpy = jasmine.createSpyObj('WsGatewayService', ['connect'], {
      notifications$: of([]),
      notificationUnread$: of(0),
      isConnected$: of(false)
    });
    const noteSpy = jasmine.createSpyObj('NotificationService', ['setOpenState', 'deleteNotification'], {
      openState$: of(false),
      closingIds: new Set<number>()
    });

    await TestBed.configureTestingModule({
      imports: [NotificationsDropdownComponent],
      providers: [
        { provide: WsGatewayService, useValue: wsSpy },
        { provide: NotificationService, useValue: noteSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationsDropdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
