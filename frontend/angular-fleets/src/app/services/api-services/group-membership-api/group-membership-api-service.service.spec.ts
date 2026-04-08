import { TestBed } from '@angular/core/testing';

import { GroupMembershipApiService } from './group-membership-api.service';

describe('GroupMembershipApiService', () => {
  let service: GroupMembershipApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupMembershipApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
