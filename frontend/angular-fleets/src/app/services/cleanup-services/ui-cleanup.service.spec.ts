import { TestBed } from '@angular/core/testing';

import { UiCleanupService } from './ui-cleanup.service';

describe('UiCleanupService', () => {
  let service: UiCleanupService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UiCleanupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
