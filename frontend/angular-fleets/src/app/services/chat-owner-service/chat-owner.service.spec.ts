import { TestBed } from '@angular/core/testing';

import { ChatOwnerService } from './chat-owner.service';

describe('ChatOwnerService', () => {
  let service: ChatOwnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChatOwnerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
