import {DestroyRef, inject, Injectable} from '@angular/core';
import {BehaviorSubject, takeUntil} from "rxjs";
import {ConversationViewModel} from "../../../models/chat/conversation-view-model";
import {SelectionModel} from "@angular/cdk/collections";
import {MessageViewModel} from "../../../models/chat/message-view-model";
import {ChatApiService} from "../../api-services/chat-api/chat-api.service";
import {UserService} from "../../user-services/user.service";
import {WsGatewayService} from "../../websocket-messaging/ws-gateway.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {SendMessageRequest} from "../../../models/chat/send-message-request";

@Injectable({
  providedIn: 'root'
})
export class ChatStoreService {
  private destroyRef = inject(DestroyRef);



  private conversationsSubject = new BehaviorSubject<ConversationViewModel[]>([]);
  public conversations$ = this.conversationsSubject.asObservable();

  private selectedConvIdSubject = new BehaviorSubject<number | null>(null);
  public selectedConvId$ = this.selectedConvIdSubject.asObservable();

  private messagesSubject = new BehaviorSubject<MessageViewModel[]>([]);
  public messages$ = this.messagesSubject.asObservable();

  constructor(private chatApi: ChatApiService,
              private userSrv: UserService,
              private ws: WsGatewayService) {

    this.ws.chatMessage$.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(msgDto => {
        this.onIncomingWsMessage(msgDto as MessageViewModel);
      });

    this.ws.chatConversation$.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(convDto => {
        this.upsertConversation(convDto as ConversationViewModel);
      })
  }

  setConversationsArr(convs: ConversationViewModel[]) {
    this.conversationsSubject.next(convs);
  }

  selectConversation(convId: number) {
    this.selectedConvIdSubject.next(convId);
  }

  setActiveMessagesArr(msgs: MessageViewModel[]) {
    this.messagesSubject.next(msgs);
  }

  upsertMessage(msg: MessageViewModel) {
    const current = this.messagesSubject.value;

    const exists = current.some(m =>
      (m.msgId && m.msgId === msg.msgId) ||
      (!!m.clientMessageId && m.clientMessageId === msg.clientMessageId)
    );
    if(exists) return;

    this.messagesSubject.next([...current, msg]);
  }

  upsertConversation(conv: ConversationViewModel) {
    const convs = this.conversationsSubject.value;
    const idx = convs.findIndex(conv => conv.conversationId === conv.conversationId);

    let next: ConversationViewModel[];
    if(idx > 0) {
      next = [...convs];
      next[idx] = {...next[idx], ...conv };

      //move to top
      const [hit] = next.splice(idx, 1);
      next.unshift(hit);
    } else {
      next = [conv, ...convs];
    }
    this.conversationsSubject.next(next);
  }


  private onIncomingWsMessage(msg: MessageViewModel) {

    //update active messages if the message belongs to selected conversation
    const selectedId = this.selectedConvIdSubject.value;
    if(selectedId && msg.conversationId === selectedId) {
      this.upsertMessage(msg);
    } else {
      //todo update unread
    }

    this.bumpConversationUpdate(msg);
  }

  bumpConversationUpdate(msg: MessageViewModel) {
    //update conversation preview ordering
    const convs = this.conversationsSubject.value;
    const idx = convs.findIndex(conv => conv.conversationId === msg.conversationId)
    if(idx >= 0) {
      const updated = [...convs];
      const hit = updated[idx];

      const newHit = {
        ...hit,
        lastMsgId: msg.msgId,
        updatedAt: msg.createdAt
      } as any;

      updated.splice(idx, 1);
      updated.unshift(newHit);
      this.conversationsSubject.next(updated);
    }
  }


}
