import { TestBed } from '@angular/core/testing';

import { QuickAccessMenuService } from './quick-access-menu.service';

describe('QuickAccessMenuService', () => {
  let service: QuickAccessMenuService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(QuickAccessMenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
