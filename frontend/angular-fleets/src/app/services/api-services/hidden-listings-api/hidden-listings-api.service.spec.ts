import { TestBed } from '@angular/core/testing';

import { HiddenListingsApiService } from './hidden-listings-api.service';

describe('HiddenListingsService', () => {
  let service: HiddenListingsApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HiddenListingsApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
