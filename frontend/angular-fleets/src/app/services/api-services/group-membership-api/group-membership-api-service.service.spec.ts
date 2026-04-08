import { TestBed } from '@angular/core/testing';

import { GroupMembershipApiServiceService } from './group-membership-api-service.service';

describe('GroupMembershipApiServiceService', () => {
  let service: GroupMembershipApiServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupMembershipApiServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
