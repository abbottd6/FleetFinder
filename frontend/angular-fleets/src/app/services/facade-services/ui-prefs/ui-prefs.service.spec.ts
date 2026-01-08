import { TestBed } from '@angular/core/testing';

import { UiPrefsService } from './ui-prefs.service';

describe('UiPrefsService', () => {
  let service: UiPrefsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UiPrefsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
