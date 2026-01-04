import { Injectable } from '@angular/core';
import {Client, IMessage, StompSubscription} from "@stomp/stompjs";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import SockJS from "sockjs-client";
import {environment} from "../../../environments/environment";
import {MessageViewModel} from "../../models/chat/message-view-model";
import {ConversationViewModel} from "../../models/chat/conversation-view-model";

export interface TotalUnreadDto {
  userId: number,
  unreadCount: number,
}

@Injectable({
  providedIn: 'root'
})
export class WsGatewayService {

  private client: Client | null = null;

  private connectedSubject = new BehaviorSubject<boolean>(false);
  public isConnected$: Observable<boolean> = this.connectedSubject.asObservable();

  private totalUnreadSubject = new BehaviorSubject<number | null>(null);
  public totalUnread$ = this.totalUnreadSubject.asObservable();

  private chatMessageSubject = new Subject<MessageViewModel>();
  public chatMessage$ = this.chatMessageSubject.asObservable();

  private chatConversationSubject = new Subject<ConversationViewModel>();
  public chatConversation$ = this.chatConversationSubject.asObservable();

  private unreadStompSubscription: StompSubscription | null = null;

  constructor() {
  }

  subscribe<T>(destination: string, handler: (body: T) => void): StompSubscription {
    if(!this.client || !this.connectedSubject.value) {
      throw new Error('WS not connected, cannot subscribe.');
    }
    return this.client.subscribe(destination, (msg) => handler(JSON.parse(msg.body) as T));
  }

  connect(token: string): void {
    if(this.client?.active) return;
    if(!token) return;

    //todo make this url not use the access token
    const wsUrl = `${environment.backendApiUrl}/websocket?access_token=${encodeURIComponent(token)}`;
    console.warn('wsUrl:', wsUrl);

    this.client = new Client({
      webSocketFactory: () => new SockJS(wsUrl),
      connectHeaders: {},
      reconnectDelay: 3000,
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,


      onConnect: () => {
        this.connectedSubject.next(true);
        console.log("STOMP connected");

        this.unreadStompSubscription = this.client!.subscribe(
          '/user/queue/chat.unread',
          (msg) => {
            try {
              this.totalUnreadSubject.next((JSON.parse(msg.body) as TotalUnreadDto).unreadCount)

            } catch {
              console.warn('Malformed ws payload', msg.body);
            }
          }
        );

        setTimeout(() => this.publish('/app/chat.total_unread', {}), 500);

        //
        // this.client!.subscribe('/user/queue/chat.message', (msg) => {
        //   const dto = JSON.parse(msg.body) as MessageViewModel;
        //   this.chatMessageSubject.next(dto);
        // });
        //
        // this.client!.subscribe('/user/queue/chat.conversation', (conv) => {
        //   const dto = JSON.parse(conv.body) as ConversationViewModel;
        //   this.chatConversationSubject.next(dto);
        // });

      },

      onStompError: () => { console.error("STOMP ERROR") },
      debug: (s) => console.log(['stomp'], s),
      onWebSocketClose: () => {
        this.connectedSubject.next(false);
        this.unreadStompSubscription?.unsubscribe();
        this.unreadStompSubscription = null;
      }
    });

    this.client.activate();
  }

  disconnect(): void {
    this.unreadStompSubscription?.unsubscribe();
    this.unreadStompSubscription = null;

    if(this.client?.active) { this.client.deactivate() }

    this.client = null as any;

    this.connectedSubject.next(false);
  }

  publish(destination: string, body: unknown): void {
    if(!this.client || !this.connectedSubject.value) return;
    this.client.publish({
      destination,
      body: JSON.stringify(body),
    })
  }

  isConnected() {
    return this.connectedSubject.value;
  }

  updateTotalUnread(dto: TotalUnreadDto) {
      this.totalUnreadSubject.next(dto.unreadCount)
  }

  jwtSub(token: string): string | null {
    try {
      const payloadPart = token.split('.')[1];
      const base64 = payloadPart.replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(
        atob(base64)
          .split('')
          .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(json).sub ?? null;
    } catch {
      return null;
    }
  }
}
