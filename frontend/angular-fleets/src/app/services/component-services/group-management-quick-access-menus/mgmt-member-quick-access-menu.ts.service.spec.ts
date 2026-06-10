import { TestBed } from '@angular/core/testing';

import { MgmtMemberQuickAccessMenuService } from './mgmt-member-quick-access-menu.service';

describe('MgmtMemberQuickAccessMenuTsService', () => {
  let service: MgmtMemberQuickAccessMenuService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MgmtMemberQuickAccessMenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
