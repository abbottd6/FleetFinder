import {DestroyRef, inject, Injectable} from '@angular/core';
import {BehaviorSubject, filter, shareReplay, Subscription, take, takeUntil, tap} from "rxjs";
import {ConversationViewModel} from "../../../models/chat/conversation-view-model";
import {MessageViewModel} from "../../../models/chat/message-view-model";

import {UserService} from "../../user-services/user.service";
import {WsGatewayService} from "../../websocket-messaging/ws-gateway.service";
import {StompSubscription} from "@stomp/stompjs";

@Injectable({
  providedIn: 'root'
})
export class ChatStoreService {
  private destroyRef = inject(DestroyRef);
  private msgSub: StompSubscription | null = null;
  private convSub: StompSubscription | null = null;

  private conversationsSubject = new BehaviorSubject<ConversationViewModel[]>([]);
  public conversations$ = this.conversationsSubject.asObservable();

  private selectedConvIdSubject = new BehaviorSubject<number | null>(null);
  public selectedConvId$ = this.selectedConvIdSubject.asObservable();

  private messagesSubject = new BehaviorSubject<MessageViewModel[]>([]);
  public messages$ = this.messagesSubject.asObservable();

  private wsConnectSub: Subscription | null = null;

  constructor(private userSrv: UserService,
              private ws: WsGatewayService) {}

  start(): void {
    if(this.msgSub || this.convSub || this.wsConnectSub) return;

   this.wsConnectSub = this.ws.isConnected$.pipe(
     filter(Boolean), take(1))
     .subscribe(() => {
       this.wsConnectSub?.unsubscribe();
       this.wsConnectSub = null;
       this.initSubscriptions();
     });
  }

  stop(): void {
    this.wsConnectSub?.unsubscribe();
    this.wsConnectSub = null;

    this.msgSub?.unsubscribe();
    this.msgSub = null;

    this.convSub?.unsubscribe();
    this.convSub = null;

    this.conversationsSubject.next([]);
    this.messagesSubject.next([]);
    this.selectedConvIdSubject.next(null);
  }

  initSubscriptions() {
    if(this.msgSub || this.convSub) return;

    this.msgSub = this.ws.subscribe('/user/queue/chat.message', (msgDto) => {
      this.onIncomingWsMessage(msgDto as MessageViewModel);
    });

    this.convSub = this.ws.subscribe('/user/queue/chat.conversation', (convDto) => {
      this.upsertConversation(convDto as ConversationViewModel);
    })
  }

  setConversationsArr(convs: ConversationViewModel[]) {
    this.conversationsSubject.next(convs);
  }

  getConversationsArr() {
    return this.conversationsSubject.value;
  }

  selectConversation(convId: number) {
    this.selectedConvIdSubject.next(convId);
  }

  clearSelectedConv() {
    this.selectedConvIdSubject.next(null);
  }

  setActiveMessagesArr(msgs: MessageViewModel[]) {
    this.messagesSubject.next(msgs.reverse());
    this.afterLoadMessages(msgs);
  }

  getActiveMessagesArr() {
    return this.messagesSubject.value;
  }

  setActiveMessagesArrNoScroll(msgs: MessageViewModel[]) {
    this.messagesSubject.next(msgs);
  }

  clearActiveMessagesArr() {
    this.messagesSubject.next([]);
  }

  upsertMessage(msg: MessageViewModel) {
    const current = this.messagesSubject.value;

    const exists = current.some(m =>
      (m.msgId && m.msgId === msg.msgId) ||
      (!!m.clientMessageId && m.clientMessageId === msg.clientMessageId)
    );
    if(exists) return;

    this.messagesSubject.next([...current, msg]);
    setTimeout(() =>
      (this.ws.publish('/app/chat.read', {
        conversationId: msg.conversationId,
        lastReadMsgId: msg.msgId })), 1000);
  }

  upsertConversation(conv: ConversationViewModel) {
    const convs = this.conversationsSubject.value;
    const idx = convs.findIndex(conv => conv.conversationId === conv.conversationId);

    console.log("IDX", idx);
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

  afterLoadMessages(msgs: MessageViewModel[]) {
    const lastIncoming = [...msgs].reverse().find(
      msg => msg.senderId !== this.userSrv.userId);
    console.log("this message", lastIncoming?.msgId);
    if(lastIncoming) {
      this.ws.publish('/app/chat.read', {
        conversationId: lastIncoming.conversationId,
        lastReadMsgId: lastIncoming.msgId });
    }
  }
}
