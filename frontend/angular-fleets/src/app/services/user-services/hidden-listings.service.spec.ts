import { TestBed } from '@angular/core/testing';

import { HiddenListingsService } from './hidden-listings.service';

describe('HiddenListingsService', () => {
  let service: HiddenListingsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HiddenListingsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
