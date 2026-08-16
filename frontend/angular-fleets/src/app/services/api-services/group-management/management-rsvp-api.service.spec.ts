import { TestBed } from '@angular/core/testing';

import { ManagementRsvpApiService } from './management-rsvp-api.service';

describe('ManagementRsvpApiService', () => {
  let service: ManagementRsvpApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManagementRsvpApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
