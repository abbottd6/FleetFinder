import { TestBed } from '@angular/core/testing';

import { MgmtInvitesQuickAccessMenuService } from './mgmt-invites-quick-access-menu.service';

describe('MgmtInvitesQuickAccessMenuService', () => {
  let service: MgmtInvitesQuickAccessMenuService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MgmtInvitesQuickAccessMenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
