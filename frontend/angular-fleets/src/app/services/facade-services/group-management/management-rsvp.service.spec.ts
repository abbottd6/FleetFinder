import { TestBed } from '@angular/core/testing';

import { ManagementRsvpService } from './management-rsvp.service';

describe('ManagementRsvpService', () => {
  let service: ManagementRsvpService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManagementRsvpService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
