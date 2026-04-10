import { TestBed } from '@angular/core/testing';

import { GroupMembershipsInteractService } from './group-memberships-interact.service';

describe('GroupMembershipsInteractService', () => {
  let service: GroupMembershipsInteractService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GroupMembershipsInteractService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
