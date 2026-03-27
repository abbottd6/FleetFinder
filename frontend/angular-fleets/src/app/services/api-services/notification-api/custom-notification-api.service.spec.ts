import { TestBed } from '@angular/core/testing';

import { NotificationSettingsApiService } from './notification-settings-api.service';

describe('CustomNotificationApiService', () => {
  let service: NotificationSettingsApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationSettingsApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
