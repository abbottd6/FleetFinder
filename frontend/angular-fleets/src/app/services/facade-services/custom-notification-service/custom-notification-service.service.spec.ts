import { TestBed } from '@angular/core/testing';

import { CustomNotificationServiceService } from './custom-notification-service.service';

describe('CustomNotificationServiceService', () => {
  let service: CustomNotificationServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CustomNotificationServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
