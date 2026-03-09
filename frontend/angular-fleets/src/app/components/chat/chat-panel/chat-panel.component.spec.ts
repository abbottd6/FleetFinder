import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, NEVER } from 'rxjs';

import { ChatPanelComponent } from './chat-panel.component';
import { ChatHostService } from '../../../services/facade-services/chat/chat-host.service';
import { ChatApiService } from '../../../services/api-services/chat-api/chat-api.service';
import { UserService } from '../../../services/user-services/user.service';
import { ChatStoreService } from '../../../services/facade-services/chat/chat-store.service';
import { MatDialog } from '@angular/material/dialog';
import { ChatOptionsMenuService } from '../../../services/facade-services/chat/chat-options-menu.service';
import { WsGatewayService } from '../../../services/websocket-messaging/ws-gateway.service';

describe('ChatPanelComponent', () => {
  let component: ChatPanelComponent;
  let fixture: ComponentFixture<ChatPanelComponent>;

  beforeEach(async () => {
    const chatHostSpy = jasmine.createSpyObj('ChatHostService', ['closeChat', 'minimizeChat', 'openChatWith'], {
      windowState$: of('NORMAL'),
      open$: of(true),
      mounted$: of(true)
    });
    const chatApiSpy = jasmine.createSpyObj('ChatApiService', ['getMyConversations', 'getConversationMessages', 'sendMessage']);
    chatApiSpy.getMyConversations.and.returnValue(of({ content: [], page: { totalElements: 0, size: 10, number: 0, totalPages: 0 } }));
    chatApiSpy.getConversationMessages.and.returnValue(of({ content: [], page: { totalElements: 0, size: 10, number: 0, totalPages: 0 } }));
    chatApiSpy.sendMessage.and.returnValue(NEVER);

    const userSpy = jasmine.createSpyObj('UserService', [], { sessionUser$: of(null), userId: 0 });
    const chatStoreSpy = jasmine.createSpyObj('ChatStoreService',
      ['setConversations', 'setSelectedConvId', 'setConversationsArr', 'setActiveMessagesArr', 'setActiveMessagesArrNoScroll', 'selectConversation', 'clearActiveMessagesArr', 'upsertMessage', 'getConversationsArr', 'getActiveMessagesArr', 'getSelectedId'], {
      conversations$: of([]),
      selectedConvId$: of(null),
      convUnreadMap$: of({}),
      messages$: of([])
    });
    const dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);
    const optionsMenuSpy = jasmine.createSpyObj('ChatOptionsMenuService', ['open', 'registerMenu', 'openContextMenu', 'onTouchStart', 'onTouchEnd'], { longPressTriggered: false });
    const wsSpy = jasmine.createSpyObj('WsGatewayService', ['connect', 'disconnect'], {
      isConnected$: of(false), perConvUnread$: of([]), notificationUnread$: of(0)
    });

    await TestBed.configureTestingModule({
      imports: [ChatPanelComponent],
      providers: [
        { provide: ChatHostService, useValue: chatHostSpy },
        { provide: ChatApiService, useValue: chatApiSpy },
        { provide: UserService, useValue: userSpy },
        { provide: ChatStoreService, useValue: chatStoreSpy },
        { provide: MatDialog, useValue: dialogSpy },
        { provide: ChatOptionsMenuService, useValue: optionsMenuSpy },
        { provide: WsGatewayService, useValue: wsSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(ChatPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
