import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";
import {MatIcon} from "@angular/material/icon";
import {AsyncPipe, DatePipe, NgIf, SlicePipe} from "@angular/common";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {ChatApiService} from "../../../services/api-services/chat-api/chat-api.service";
import {ConversationViewModel} from "../../../models/chat/conversation-view-model";
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from "@angular/material/table";
import {SelectionModel} from "@angular/cdk/collections";
import {MeasureFooterHeightDirective} from "../../../utils/measure-footer-height-directive";

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
    SlicePipe
  ],
  styleUrl: './chat-panel.component.css'
})
export class ChatPanelComponent implements OnInit, OnDestroy {
  private chatPanelDestroy$ = new Subject<void>();
  @Input() open!: boolean;

  convPageIdx: number = 0;
  convPageSize: number = 10;
  convTotalElements: number = 0;

  convColumns: string[] = ['convDetails'];
  convDataSource = new MatTableDataSource<ConversationViewModel>();
  selectedConv =  new SelectionModel<ConversationViewModel>(false, []);
  noConvs: boolean = true;

  constructor(private chatHostSrv: ChatHostService, private chatApi: ChatApiService) {}

  ngOnInit() {
    this.getMyConversations(this.convPageIdx, this.convPageSize);
  }

  terminateChat(){
    this.chatHostSrv.closeChat()
  }

  toggleWindowState() {
    this.chatHostSrv.toggleWindowState();
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

  ngOnDestroy() {
    this.chatPanelDestroy$.next();
    this.chatPanelDestroy$.complete();
  }
}
