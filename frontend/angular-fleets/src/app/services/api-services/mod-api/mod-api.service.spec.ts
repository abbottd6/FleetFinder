import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { ModApiService } from './mod-api.service';

describe('ModApiService', () => {
  let service: ModApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ModApiService]
    });
    service = TestBed.inject(ModApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
