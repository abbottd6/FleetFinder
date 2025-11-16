import { TestBed } from '@angular/core/testing';

import { ListingFormService } from './listing-form.service';

describe('ListingFormService', () => {
  let service: ListingFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListingFormService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
