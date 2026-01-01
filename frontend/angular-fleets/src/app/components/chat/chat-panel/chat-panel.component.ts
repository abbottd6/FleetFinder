import {AfterViewInit, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";
import {MatIcon} from "@angular/material/icon";
import {AsyncPipe, DatePipe, NgIf, SlicePipe} from "@angular/common";
import {BehaviorSubject, distinctUntilChanged, map, Observable, shareReplay, Subject, takeUntil} from "rxjs";
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

  convPageIdx: number = 0;
  convPageSize: number = 10;
  convTotalElements: number = 0;

  convColumns: string[] = ['convDetails'];
  convDataSource = new MatTableDataSource<ConversationViewModel>();
  selectedConv =  new SelectionModel<ConversationViewModel>(false, []);
  noConvs: boolean = true;

  msgPageIdx: number = 0;
  msgPageSize: number = 25;
  msgTotalElements: number = 0;
  noMsgs: boolean = true;

  private messagesSubject = new BehaviorSubject<MessageViewModel[]>([]);
  protected messages$ = this.messagesSubject.asObservable();

  constructor(protected chatHostSrv: ChatHostService,
              private chatApi: ChatApiService,
              private userSrv: UserService) {}

  ngOnInit() {
    this.getMyConversations(this.convPageIdx, this.convPageSize);
  }

  ngAfterViewInit() {
    this.chatLayoutMode$
      .pipe(distinctUntilChanged())
      .subscribe(mode => {
        if(mode === 'handheld') this.drawer.close();
        else this.drawer.open();
        console.log("chatLayoutMode:", mode);
      })
  }

  terminateChat(){
    this.chatHostSrv.closeChat()
  }

  toggleWindowState() {
    this.isCollapsing = true;
    this.chatHostSrv.toggleWindowState();
    setTimeout(() => this.isCollapsing = false, 300);
  }

  getMyConversations(idx: number, size: number) {
    this.chatApi.getMyConversations(idx, size)
      .pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe({
        next: (page) => {
          this.convDataSource.data = page.content;
          this.convTotalElements = page.totalElements;
          this.convPageSize = page.size;
          this.convPageIdx = page.number;
          this.noConvs = (this.convDataSource.data.length === 0);
        }
      })
  }

  sendDmMessage(input: string) {
    this.chatHostSrv.authCheckOrRedirect();
    const userId = this.userSrv.getUserId()
    const type: string = 'TEXT';
    console.log("DID WE GET HERE?", input);

    const msg = new SendMessageRequest(this.selectedConv.selected[0], userId, type, input);

    this.chatApi.sendMessage(msg).pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe({
        next: (msg) => {
          this.appendMsg(msg)
        }
      })
  }

  appendMsg(msg: MessageViewModel) {
    this.messagesSubject.next([...this.messagesSubject.value, msg]);
  }

  onConvClick(conv: ConversationViewModel) {
    this.selectedConv.select(conv);

    // const msg = new MessageViewModel(7, 1, 1, 'marydoe',
    //   'DIRECT', 'wow a message for me', new Date(), new Date(), null, null,
    //   'clientmsgid');
    // this.appendMsg(msg);

    this.loadConvMessages(this.selectedConv);
  }

  loadConvMessages(selectionConv: SelectionModel<ConversationViewModel>) {
    this.chatApi.getConversationMessages(this.msgPageIdx, this.msgPageSize, selectionConv.selected[0].conversationId)
      .pipe(takeUntil(this.chatPanelDestroy$))
      .subscribe({
        next: (page) => {
          this.messagesSubject.next(page.content);
          this.msgTotalElements = page.totalElements;
          this.msgPageSize = page.size;
          this.msgPageIdx = page.number;
          this.noMsgs = page.content.length === 0;
        }
      })
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
    this.chatPanelDestroy$.next();
    this.chatPanelDestroy$.complete();
  }
}
