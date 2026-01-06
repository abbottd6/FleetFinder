import {AfterViewInit, Component, ElementRef, inject, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";
import {MatIcon} from "@angular/material/icon";
import {AsyncPipe, DatePipe, NgIf, SlicePipe} from "@angular/common";
import {distinctUntilChanged, filter, map, Observable, shareReplay, Subject, take, takeUntil,} from "rxjs";
import {ChatApiService} from "../../../services/api-services/chat-api/chat-api.service";
import {ConversationViewModel} from "../../../models/chat/conversation-view-model";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MatSidenav, MatSidenavContainer, MatSidenavContent} from "@angular/material/sidenav";
import {LayoutMode} from "../../input-fields/search-bar/search-bar.component";
import {BreakpointObserver} from "@angular/cdk/layout";
import {MatButton} from "@angular/material/button";
import {ChatWindowState, collapsed, expanded} from "../shell-component/chat-shell.component";
import {SendMessageRequest} from "../../../models/chat/send-message-request";
import {UserService} from "../../../services/user-services/user.service";
import {MessageComponent} from "../message/message.component";
import {MessageInputComponent} from "../message-input/message-input.component";
import {ChatStoreService} from "../../../services/facade-services/chat/chat-store.service";
import {WsGatewayService} from "../../../services/websocket-messaging/ws-gateway.service";
import {MatBadge} from "@angular/material/badge";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../../pop-ups/confirm-generic/confirm-generic.component";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {ChatOptionsMenuService} from "../../../services/facade-services/chat/chat-options-menu.service";

@Component({
  selector: 'app-chat-panel',
  standalone: true,
  templateUrl: './chat-panel.component.html',
  imports: [
    MatIcon,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    DatePipe,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    NgIf,
    SlicePipe,
    MatSidenav,
    AsyncPipe,
    MatSidenavContainer,
    MatSidenavContent,
    MatButton,
    MessageComponent,
    MessageInputComponent,
    MatBadge,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
  ],
  styleUrl: './chat-panel.component.css'
})
export class ChatPanelComponent implements OnInit, OnDestroy, AfterViewInit {
  protected ws = inject(WsGatewayService);
  badgeSize: 'small' | 'medium' | 'large' = 'medium';
  ChatWindowState = ChatWindowState;

  private chatPanelDestroy$ = new Subject<void>();
  private breakpointObserver = new BreakpointObserver();
  protected isCollapsing: boolean = false;

  @Input() open!: boolean;
  @ViewChild('drawer') drawer!: MatSidenav;

  @ViewChild('msgScroll') msgScroll!: ElementRef<HTMLElement>;
  private suppressAutoScrollUntil = 0;

  @ViewChild(MatMenuTrigger) menuTrigger!: MatMenuTrigger;
  @ViewChild('chatMenuAnchor', { read: ElementRef })
  protected chatMenuAnchor!: ElementRef<HTMLElement>;

  convPageIdx: number = 0;
  convPageSize: number = 10;
  convTotalElements: number = 0;
  convTotalPages: number = 0;
  noConvs: boolean = true;

  msgPageIdx: number = 0;
  msgPageSize: number = 50;
  msgTotalElements: number = 0;
  msgTotalPages: number = 0;
  noMsgs: boolean = true;

  convColumns: string[] = ['convDetails'];
  convDataSource = new MatTableDataSource<ConversationViewModel>();
  selectedConv =  new SelectionModel<ConversationViewModel>(false, []);

  readonly convUnreadMap$ = this.ws.perConvUnread$.pipe(
    map(dtos=> (dtos ?? [])
        .reduce<Record<number, number>>((accumulator, dto) => {
        accumulator[dto.conversationId] = dto.unreadCount;
        return accumulator;
      }, {})),
      shareReplay({ bufferSize: 1, refCount: true })
  );

  constructor(protected chatHostSrv: ChatHostService,
              private chatApi: ChatApiService,
              private userSrv: UserService,
              protected chatStoreSrv: ChatStoreService,
              private dialog: MatDialog,
              private optionsMenu: ChatOptionsMenuService) {

    this.resetConvPage();
    this.resetMsgPage();
  }

  ngOnInit() {
    this.getMyConversations(this.convPageIdx, this.convPageSize);

    this.chatStoreSrv.conversations$.pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(convs => {
        this.convDataSource.data = convs;

        const selectedId = this.selectedConv.selected[0]?.conversationId;
        if(selectedId) {
          const newRef = convs.find(
            con => con.conversationId === selectedId);
          if(newRef) {
            this.selectedConv.select(newRef);
          }
        }
      });
  }

  ngAfterViewInit() {
    this.chatLayoutMode$.pipe(
      distinctUntilChanged(),
      takeUntil(this.chatPanelDestroy$))
      .subscribe(mode => {
        if(mode === 'handheld') this.drawer.close();
        else this.drawer.open();
      })

    this.chatStoreSrv.messages$.pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(() => {
        if(!this.shouldAutoScroll()) {
          this.suppressAutoScrollUntil = 0;
          return;
        }
        setTimeout(() => this.scrollMsgsToBottom(), 300);
      })

    this.chatStoreSrv.selectedConvId$.pipe(
      filter((id): id is number => id != null),
      distinctUntilChanged(),
      takeUntil(this.chatPanelDestroy$))
      .subscribe((selectedId) => {
        const conv = this.convDataSource.data.find(
          conv => conv.conversationId === selectedId);
        if(conv) {
          this.onConvClick(conv);
        }
      })

    this.optionsMenu.registerMenu(this.menuTrigger, this.chatMenuAnchor)
  }

  private shouldAutoScroll(): boolean {
    return Date.now() >= this.suppressAutoScrollUntil
  }
  suppressAutoScroll(ms = 5000) {
    this.suppressAutoScrollUntil = Date.now() + ms;
  }

  getMyConversations(idx: number, size: number) {
    this.chatApi.getMyConversations(idx, size)
      .pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(page => {
        this.convTotalElements = page.page.totalElements;
        this.convPageSize = page.page.size;
        this.convPageIdx = page.page.number;
        this.convTotalPages = page.page.totalPages;
        this.noConvs = (page.content.length === 0);

        this.chatStoreSrv.setConversationsArr(page.content);

        if(this.chatHostSrv.convOnHold) {
          const id = this.chatHostSrv.convOnHold

          if (id) {
            const select = this.convDataSource.data.find(
              conv => conv.conversationId === id);
            if (select) {
              setTimeout(() => this.onConvClick(select), 200);
            }
          }
          this.chatHostSrv.convOnHold = null;
        }
      });
  }

  loadSelectedConversationMessages() {
    const conv = this.selectedConv.selected[0];
    this.chatApi.getConversationMessages(this.msgPageIdx, this.msgPageSize, conv.conversationId)
      .pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(page => {
        this.msgTotalElements = page.page.totalElements;
        this.msgPageSize = page.page.size;
        this.msgPageIdx = page.page.number;
        this.msgTotalPages = page.page.totalPages;
        this.noMsgs = page.content.length === 0;

        this.chatStoreSrv.setActiveMessagesArr(page.content);
      })
  }

  sendDmMessage(input: string) {
    const conv = this.selectedConv.selected[0];
    this.chatStoreSrv.selectConversation(conv.conversationId);
    const userId = this.userSrv.userId;

    if(!userId) return;

    const msg = new SendMessageRequest(this.selectedConv.selected[0], userId, 'TEXT', input);

    this.chatApi.sendMessage(msg).pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(sent => {
        this.chatStoreSrv.upsertMessage(sent);
      });
  }

  contextMenu(event: MouseEvent, conv: ConversationViewModel) {
    this.selectedConv.select(conv);
    this.chatStoreSrv.selectConversation(conv.conversationId);
    this.optionsMenu.openContextMenu(event);
  }

  touchMenu(event: TouchEvent, conv: ConversationViewModel) {
    this.selectedConv.select(conv);
    this.chatStoreSrv.selectConversation(conv.conversationId);
    this.optionsMenu.onTouchStart(event);
  }

  unTouchMenu(event: TouchEvent) {
    this.optionsMenu.onTouchEnd(event);
  }

  archiveConversation(conv: ConversationViewModel) {
    const message: string = 'Conversation will be archived. New messages in the conversation will restore it.';
    const title: string = 'Conversation with: ' + conv.otherUserName;

    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        message: message,
        title: title
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.chatApi.archiveConvAndReturnConvs(conv.conversationId, this.convPageIdx, this.convPageSize)
          .pipe(takeUntil(this.chatPanelDestroy$))
          .subscribe(page => {
            this.chatStoreSrv.clearActiveMessagesArr();
            this.selectedConv.clear();

            this.convTotalElements = page.page.totalElements;
            this.convPageSize = page.page.size;
            this.convPageIdx = page.page.number;
            this.convTotalPages = page.page.totalPages;
            this.noConvs = (page.content.length === 0);

            this.chatStoreSrv.setConversationsArr(page.content);
          })
      }
    });
  }

  muteConversation(conv: ConversationViewModel) {
    const message: string = 'Conversation will be muted. You will no longer ' +
      ' receive messages from this user.\n\nSending a message to them later will require confirmation to unmute.'

    const title: string = 'Conversation with: ' + conv.otherUserName;

    const dialogRef = this.dialog.open(ConfirmGenericComponent, {
      data: {
        message: message,
        title: title
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.chatStoreSrv.clearActiveMessagesArr();
        this.selectedConv.clear();
        this.chatApi.muteConvAndReturnConvs(conv.conversationId, this.convPageIdx, this.convPageSize)
          .pipe(takeUntil(this.chatPanelDestroy$))
          .subscribe(page => {
            this.chatStoreSrv.clearActiveMessagesArr();
            this.selectedConv.clear();

            this.convTotalElements = page.page.totalElements;
            this.convPageSize = page.page.size;
            this.convPageIdx = page.page.number;
            this.convTotalPages = page.page.totalPages;
            this.noConvs = (page.content.length === 0);

            this.chatStoreSrv.setConversationsArr(page.content);
          })
      }
    });
  }

  getNextConvPage() {
    this.convPageIdx++;
    this.chatApi.getMyConversations(this.convPageIdx, this.convPageSize)
      .pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(page => {
        this.convTotalElements = page.page.totalElements;
        this.convPageSize = page.page.size;
        this.convPageIdx = page.page.number;
        this.convTotalPages = page.page.totalPages;
        this.noConvs = (page.content.length === 0);

        const displayConvs = this.chatStoreSrv.getConversationsArr().concat(page.content);

        this.chatStoreSrv.setConversationsArr(displayConvs);
      })
  }

  getNextMessagePage() {
    const conv = this.selectedConv.selected[0];
    this.msgPageIdx++;
    this.chatApi.getConversationMessages(this.msgPageIdx, this.msgPageSize, conv.conversationId)
      .pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(page => {
        this.msgTotalElements = page.page.totalElements;
        this.msgPageSize = page.page.size;
        this.msgPageIdx = page.page.number;
        this.msgTotalPages = page.page.totalPages;
        this.noMsgs = page.content.length === 0;

        const displayedMsgs = (page.content.reverse()).concat(this.chatStoreSrv.getActiveMessagesArr());

        this.suppressAutoScroll();
        this.chatStoreSrv.setActiveMessagesArrNoScroll(displayedMsgs);
      })
  }

  scrollMsgsToBottom(smooth = false) {
    const element = this.msgScroll?.nativeElement;
    if(!element) return;

    element.scrollTo({
      top: element.scrollHeight,
      behavior: 'smooth',
    });
  }

  terminateChat(){
    this.resetMsgPage();
    this.resetConvPage();
    this.chatHostSrv.closeChat()
  }

  toggleWindowState() {
    this.isCollapsing = true;
    const newState = this.chatHostSrv.toggleWindowState();
    setTimeout(() => this.isCollapsing = false, 220);

    if(newState === collapsed) {
      this.chatStoreSrv.selectedConvId$.pipe(
        filter((id): id is number => id != null),
        take(1)).subscribe(
        id => this.chatHostSrv.convOnHold = id);

      this.selectedConv.clear();
      this.chatStoreSrv.clearSelectedConv();
      this.chatStoreSrv.clearActiveMessagesArr();
    }

    if(newState === expanded) {
      const id = this.chatHostSrv.convOnHold

      if(id) {
        const select = this.convDataSource.data.find(
          conv => conv.conversationId === id);
        if(select) {
          setTimeout(() => this.onConvClick(select), 200);
        }
      }
      this.chatHostSrv.convOnHold = null;
    }
  }

  onConvClick(conv: ConversationViewModel) {
    this.resetMsgPage();
    this.selectedConv.select(conv);
    this.chatStoreSrv.selectConversation(conv.conversationId);

    this.loadSelectedConversationMessages();
  }

  isSelected(conv: ConversationViewModel): boolean {
    const selected = this.selectedConv.selected[0];
    return !!selected && conv.conversationId === selected.conversationId;
  }


  chatLayoutMode$: Observable<LayoutMode> = this.breakpointObserver
    .observe([
      '(max-width: 620px)',
      '(min-width: 621px) and (max-width: 1250px)',
      '(min-width: 1251px)'
    ])
    .pipe(
      map(state => {
        if (state.breakpoints['(max-width: 620px)']) {
          return 'handheld';
        }
        if (state.breakpoints['(min-width: 621px) and (max-width: 1250px)']) {
          return 'mobile';
        }

        return 'full';
      }),
      shareReplay(1)
    );

  resetConvPage() {
    this.convPageIdx = 0;
    this.convPageSize = 10;
    this.convTotalElements = 0;
    this.noConvs = true;
  }

  resetMsgPage() {
    this.msgPageIdx = 0;
    this.msgPageSize = 50;
    this.msgTotalElements = 0;
    this.noMsgs = true;
  }

  isSameDay(compareTs: string | Date | number | null) {
    if(compareTs == null) return null;
    const tsDate = compareTs instanceof Date ? compareTs : new Date(compareTs);
    const now = new Date();
    return (tsDate.getFullYear() === now.getFullYear()
      && tsDate.getMonth() === now.getMonth()
      && tsDate.getDate() === now.getDate());
  }

  ngOnDestroy() {
    this.selectedConv.clear();
    this.chatPanelDestroy$.next();
    this.chatPanelDestroy$.complete();
  }
}
