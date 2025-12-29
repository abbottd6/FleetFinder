import { TestBed } from '@angular/core/testing';

import { ChatHostService } from './chat-host.service';

describe('ChatOwnerService', () => {
  let service: ChatHostService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChatHostService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
