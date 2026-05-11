import { TestBed } from '@angular/core/testing';

import { DropListRegistryService } from './drop-list-registry.service';

describe('DropListRegistryService', () => {
  let service: DropListRegistryService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DropListRegistryService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
