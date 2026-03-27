import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { BookmarkApiService } from './bookmark-api.service';
import { AuthService } from '../../auth/auth-services/auth.service';

describe('BookmarkApiService', () => {
  let service: BookmarkApiService;
  let httpMock: HttpTestingController;

  const mockAuthService = {
    isLoggedIn$: of(false)
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        BookmarkApiService,
        { provide: AuthService, useValue: mockAuthService }
      ]
    });
    service = TestBed.inject(BookmarkApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getBookmarks() should POST to /api/users/my/bookmarks_get with page params', () => {
    const mockResponse: any = {
      content: [{ idGroup: 1, title: 'Saved Group' }],
      page: { size: 10, number: 0, totalElements: 1, totalPages: 1 }
    };

    service.getBookmarks(0, 10).subscribe((response: any) => {
      expect(response.content.length).toBe(1);
      expect(response.content[0].idGroup).toBe(1);
    });

    const req = httpMock.expectOne(req => req.url.includes('/users/my/bookmarks_get'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ pageIdx: 0, pageSize: 10 });
    req.flush(mockResponse);
  });

  it('addBookmark() should POST to /api/users/my/bookmarks and trigger a refresh', () => {
    const mockRequest: any = { groupId: 5 };

    service.addBookmark(mockRequest).subscribe();

    const req = httpMock.expectOne(req =>
      req.url.includes('/users/my/bookmarks') && !req.url.includes('_get') && !req.url.includes('_brief')
    );
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);
    req.flush({ success: true });
  });

  it('deleteBookmark() should send DELETE to /api/users/my/bookmarks/{id}', () => {
    service.deleteBookmark(3).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/users/my/bookmarks/3'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
