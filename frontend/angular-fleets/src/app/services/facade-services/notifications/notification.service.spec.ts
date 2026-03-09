import { TestBed } from '@angular/core/testing';
import { of, NEVER } from 'rxjs';

import { NotificationService } from './notification.service';
import { WsGatewayService } from '../../websocket-messaging/ws-gateway.service';
import { NotificationApiService } from '../../api-services/notification-api/notification-api.service';

describe('NotificationService', () => {
  let service: NotificationService;
  let wsSpy: jasmine.SpyObj<WsGatewayService>;
  let noteApiSpy: jasmine.SpyObj<NotificationApiService>;

  beforeEach(() => {
    wsSpy = jasmine.createSpyObj('WsGatewayService', ['publish', 'setNotesArray', 'getNotesArray'], {
      'isConnected$': of(false),
      'notifications$': of([]),
    });
    wsSpy.getNotesArray.and.returnValue([]);

    noteApiSpy = jasmine.createSpyObj('NotificationApiService', ['getMyNotifications', 'deleteNotification']);
    noteApiSpy.getMyNotifications.and.returnValue(NEVER);
    noteApiSpy.deleteNotification.and.returnValue(NEVER);

    TestBed.configureTestingModule({
      providers: [
        NotificationService,
        { provide: WsGatewayService, useValue: wsSpy },
        { provide: NotificationApiService, useValue: noteApiSpy },
      ]
    });

    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('openState$ starts as false', (done) => {
    service.openState$.subscribe(open => {
      expect(open).toBeFalse();
      done();
    });
  });

  it('setOpenState(true) updates openState$ to true', (done) => {
    service.setOpenState(true);

    service.openState$.subscribe(open => {
      expect(open).toBeTrue();
      done();
    });
  });
});
