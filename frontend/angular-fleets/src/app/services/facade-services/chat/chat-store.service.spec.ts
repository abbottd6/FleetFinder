import { TestBed } from '@angular/core/testing';
import { of, NEVER } from 'rxjs';

import { ChatStoreService } from './chat-store.service';
import { UserService } from '../../user-services/user.service';
import { WsGatewayService } from '../../websocket-messaging/ws-gateway.service';

describe('ChatStoreService', () => {
  let service: ChatStoreService;
  let userSpy: jasmine.SpyObj<UserService>;
  let wsSpy: jasmine.SpyObj<WsGatewayService>;

  beforeEach(() => {
    userSpy = jasmine.createSpyObj('UserService', [], {
      'sessionUser$': of(null),
      'userId': null,
    });

    wsSpy = jasmine.createSpyObj('WsGatewayService', ['subscribe', 'publish', 'isConnected'], {
      'isConnected$': of(false),
    });

    TestBed.configureTestingModule({
      providers: [
        ChatStoreService,
        { provide: UserService, useValue: userSpy },
        { provide: WsGatewayService, useValue: wsSpy },
      ]
    });

    service = TestBed.inject(ChatStoreService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('conversations$ emits an empty array initially', (done) => {
    service.conversations$.subscribe(convs => {
      expect(convs).toEqual([]);
      done();
    });
  });

  it('selectedConvId$ emits null initially', (done) => {
    service.selectedConvId$.subscribe(id => {
      expect(id).toBeNull();
      done();
    });
  });
});
