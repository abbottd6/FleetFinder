import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { NotificationApiService } from './notification-api.service';

describe('NotificationApiService', () => {
  let service: NotificationApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NotificationApiService]
    });
    service = TestBed.inject(NotificationApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getMyNotifications() should POST to /api/notify/my_notifications with page params', () => {
    const mockResponse: any = { content: [], page: { size: 10, number: 0, totalElements: 0, totalPages: 0 } };

    service.getMyNotifications(0, 10).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(req => req.url.includes('/notify/my_notifications'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ pageIdx: 0, pageSize: 10 });
    req.flush(mockResponse);
  });

  it('deleteNotification() should send DELETE to /api/notify/delete/{id}', () => {
    service.deleteNotification(7).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/notify/delete/7'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('deleteAllMyNotifications() should send DELETE to the delete_all endpoint', () => {
    service.deleteAllMyNotifications().subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/notify/delete_all'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
