import { TestBed } from '@angular/core/testing';

import { TemplatesModalService } from './templates-modal.service';

describe('UserTemplatesComponentService', () => {
  let service: TemplatesModalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TemplatesModalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
