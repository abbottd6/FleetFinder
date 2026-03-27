import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { of, NEVER } from 'rxjs';

import { ChatHostService } from './chat-host.service';
import { AuthService } from '../../auth/auth-services/auth.service';
import { ChatApiService } from '../../api-services/chat-api/chat-api.service';
import { ChatStoreService } from './chat-store.service';

describe('ChatHostService', () => {
  let service: ChatHostService;
  let authSpy: jasmine.SpyObj<AuthService>;
  let routerSpy: jasmine.SpyObj<Router>;
  let chatApiSpy: jasmine.SpyObj<ChatApiService>;
  let chatStoreSpy: jasmine.SpyObj<ChatStoreService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  beforeEach(() => {
    authSpy = jasmine.createSpyObj('AuthService', ['login', 'logout'], {
      'isLoggedIn$': of(false),
      'tokenReady$': NEVER,
    });

    routerSpy = jasmine.createSpyObj('Router', ['navigate', 'navigateByUrl'], {
      'url': '/',
    });

    chatApiSpy = jasmine.createSpyObj('ChatApiService', [
      'conversationProvision', 'unMuteConvAndReturn'
    ]);

    chatStoreSpy = jasmine.createSpyObj('ChatStoreService', [
      'start', 'stop', 'selectConversation', 'clearSelectedConv',
      'clearActiveMessagesArr', 'upsertConversation',
    ]);

    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    TestBed.configureTestingModule({
      providers: [
        ChatHostService,
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ChatApiService, useValue: chatApiSpy },
        { provide: ChatStoreService, useValue: chatStoreSpy },
        { provide: MatDialog, useValue: dialogSpy },
      ]
    });

    service = TestBed.inject(ChatHostService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('mounted$ starts as false', (done) => {
    service.mounted$.subscribe(mounted => {
      expect(mounted).toBeFalse();
      done();
    });
  });

  it('open$ starts as false', (done) => {
    service.open$.subscribe(open => {
      expect(open).toBeFalse();
      done();
    });
  });

  it('closeChat() calls chatStoreSrv.stop()', () => {
    service.closeChat();
    expect(chatStoreSpy.stop).toHaveBeenCalled();
  });
});
