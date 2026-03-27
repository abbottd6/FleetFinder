import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { HiddenListingsApiService } from './hidden-listings-api.service';
import { AuthService } from '../../auth/auth-services/auth.service';

describe('HiddenListingsApiService', () => {
  let service: HiddenListingsApiService;
  let httpMock: HttpTestingController;

  const mockAuthService = {
    isLoggedIn$: of(false)
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        HiddenListingsApiService,
        { provide: AuthService, useValue: mockAuthService }
      ]
    });
    service = TestBed.inject(HiddenListingsApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('addHidden() should POST to /api/users/my/hidden:add with the request body', () => {
    const mockRequest: any = { groupId: 10 };

    service.addHidden(mockRequest).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/users/my/hidden:add'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);
    req.flush({ success: true });
  });

  it('clearHidden() should send DELETE to /api/users/my/hidden:clear', () => {
    service.clearHidden().subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/users/my/hidden:clear'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('undoLastHide() should send DELETE to /api/users/my/hidden:pop', () => {
    service.undoLastHide().subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/users/my/hidden:pop'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
