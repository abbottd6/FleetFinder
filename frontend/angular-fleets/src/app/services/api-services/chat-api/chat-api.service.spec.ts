import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ChatApiService } from './chat-api.service';

describe('ChatApiService', () => {
  let service: ChatApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ChatApiService]
    });
    service = TestBed.inject(ChatApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('getMyConversations() should POST to /api/chat/my_conversations with page params', () => {
    service.getMyConversations(0, 10).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/chat/my_conversations'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ pageIdx: 0, pageSize: 10 });
    req.flush({ content: [], page: { size: 10, number: 0, totalElements: 0, totalPages: 0 } });
  });

  it('sendMessage() should POST to /api/chat/send_message and return a MessageViewModel', () => {
    const mockRequest: any = { conversationId: 1, senderId: 42, messageType: 'TEXT', content: 'hello' };
    const mockResponse: any = { messageId: 99, content: 'hello', senderId: 42 };

    service.sendMessage(mockRequest).subscribe((response: any) => {
      expect(response.messageId).toBe(99);
      expect(response.content).toBe('hello');
    });

    const req = httpMock.expectOne(req => req.url.includes('/chat/send_message'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);
    req.flush(mockResponse);
  });

  it('getConversationMessages() should POST to /api/chat/conv_messages with conversationId', () => {
    service.getConversationMessages(0, 20, 5).subscribe();

    const req = httpMock.expectOne(req => req.url.includes('/chat/conv_messages'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ pageIdx: 0, pageSize: 20, conversationId: 5 });
    req.flush({ content: [], page: { size: 20, number: 0, totalElements: 0, totalPages: 0 } });
  });
});
