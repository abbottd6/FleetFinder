import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ListingTemplatesApiService } from './listing-templates-api.service';

describe('ListingTemplatesApiService', () => {
  let service: ListingTemplatesApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ListingTemplatesApiService]
    });
    service = TestBed.inject(ListingTemplatesApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getTemplates() should POST to /api/users/my/templates/get with pagination and sort params', () => {
    const mockResponse: any = {
      content: [{ templateId: 1, title: 'My Template' }],
      page: { size: 5, number: 0, totalElements: 1, totalPages: 1 }
    };

    service.getTemplates(0, 5, 'DESC', 'createdAt').subscribe(response => {
      expect(response.content.length).toBe(1);
      expect(response.content[0].templateId).toBe(1);
    });

    const req = httpMock.expectOne(req => req.url.includes('/users/my/templates/get'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ page: 0, size: 5, sortDirection: 'DESC', sortField: 'createdAt' });
    req.flush(mockResponse);
  });

  it('createTemplate() should POST to /api/users/my/templates/save with the request body', () => {
    const mockRequest: any = { title: 'New Template', description: 'Test' };

    service.createTemplate(mockRequest).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/users/my/templates/save'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);
    req.flush({ templateId: 10 });
  });

  it('deleteTemplate() should send DELETE to /api/users/my/templates/delete/{id}', () => {
    service.deleteTemplate(3).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/users/my/templates/delete/3'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
