import {AfterViewInit, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";
import {MatIcon} from "@angular/material/icon";
import {AsyncPipe, DatePipe, NgIf, SlicePipe} from "@angular/common";
import {BehaviorSubject, distinctUntilChanged, map, Observable, shareReplay, Subject, take, takeUntil, tap} from "rxjs";
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
import {ChatWindowState} from "../shell-component/chat-shell.component";
import {MessageViewModel} from "../../../models/chat/message-view-model";
import {SendMessageRequest} from "../../../models/chat/send-message-request";
import {UserService} from "../../../services/user-services/user.service";
import {MessageComponent} from "../message/message.component";
import {MessageInputComponent} from "../message-input/message-input.component";
import {ChatStoreService} from "../../../services/facade-services/chat/chat-store.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

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
    MessageInputComponent
  ],
  styleUrl: './chat-panel.component.css'
})
export class ChatPanelComponent implements OnInit, OnDestroy, AfterViewInit {
  private chatPanelDestroy$ = new Subject<void>();
  private breakpointObserver = new BreakpointObserver();
  protected isCollapsing: boolean = false;
  ChatWindowState = ChatWindowState;
  @Input() open!: boolean;
  @ViewChild('drawer') drawer!: MatSidenav;
  @ViewChild('msgScroll') msgScroll!: ElementRef<HTMLElement>;

  convPageIdx: number = 0;
  convPageSize: number = 10;
  convTotalElements: number = 0;
  noConvs: boolean = true;

  msgPageIdx: number = 0;
  msgPageSize: number = 25;
  msgTotalElements: number = 0;
  noMsgs: boolean = true;

  convColumns: string[] = ['convDetails'];
  convDataSource = new MatTableDataSource<ConversationViewModel>();
  selectedConv =  new SelectionModel<ConversationViewModel>(false, []);

  protected messages$!: Observable<MessageViewModel[]>;

  constructor(protected chatHostSrv: ChatHostService,
              private chatApi: ChatApiService,
              private userSrv: UserService,
              protected chatStoreSrv: ChatStoreService) {}

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

    this.messages$ = this.chatStoreSrv.messages$;
  }

  ngAfterViewInit() {
    this.chatLayoutMode$.pipe(
      distinctUntilChanged(),
      takeUntil(this.chatPanelDestroy$))
      .subscribe(mode => {
        if(mode === 'handheld') this.drawer.close();
        else this.drawer.open();
      })

    this.messages$.pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(() => {
        setTimeout(() => this.scrollMsgsToBottom(), 300);
      })
  }

  getMyConversations(idx: number, size: number) {
    this.chatApi.getMyConversations(idx, size)
      .pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(page => {
        this.convTotalElements = page.totalElements;
        this.convPageSize = page.size;
        this.convPageIdx = page.number;
        this.noConvs = (page.content.length === 0);

        this.chatStoreSrv.setConversationsArr(page.content);

        // MOVED THIS TO THE SUBSCRIPTION IN ON INIT
        // const selected = this.selectedConv.selected[0];
        // if (selected) {
        //   const stillThere = page.content.find(
        //     conv => conv.conversationId === selected.conversationId);
        //   if (!stillThere) this.selectedConv.clear();
        // }
      });
  }

  loadSelectedConversationMessages() {
    const conv = this.selectedConv.selected[0];
    this.chatApi.getConversationMessages(this.msgPageIdx, this.msgPageSize, conv.conversationId)
      .pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe(page => {
        this.msgTotalElements = page.totalElements;
        this.msgPageSize = page.size;
        this.msgPageIdx = page.number;
        this.noMsgs = page.content.length === 0;

        this.chatStoreSrv.setActiveMessagesArr(page.content);
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

  terminateChat(){
    this.chatHostSrv.closeChat()
  }

  toggleWindowState() {
    this.isCollapsing = true;
    this.chatHostSrv.toggleWindowState();
    setTimeout(() => this.isCollapsing = false, 220);
  }

  onConvClick(conv: ConversationViewModel) {
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
