import { Injectable } from '@angular/core';
import {Client, IMessage, StompSubscription} from "@stomp/stompjs";
import {BehaviorSubject, Observable, Subject, take} from "rxjs";
import {OidcSecurityService} from "angular-auth-oidc-client";
import SockJS from "sockjs-client";
import {environment} from "../../../environments/environment";
import {MessageViewModel} from "../../models/chat/message-view-model";
import {ConversationViewModel} from "../../models/chat/conversation-view-model";

export type UnreadCountsPayload = {
  totalUnread: number;
  perConversation: Record<number, number>;
}

@Injectable({
  providedIn: 'root'
})
export class WsGatewayService {

  private client: Client | null = null;

  private connectedSubject = new BehaviorSubject<boolean>(false);
  public isConnected$: Observable<boolean> = this.connectedSubject.asObservable();

  private unreadCountSubject = new BehaviorSubject<UnreadCountsPayload | null>(null);
  public unreadCounts$ = this.unreadCountSubject.asObservable();

  private chatMessageSubject = new Subject<MessageViewModel>();
  public chatMessage$ = this.chatMessageSubject.asObservable();

  private chatConversationSubject = new Subject<ConversationViewModel>();
  public chatConversation$ = this.chatConversationSubject.asObservable();

  private unreadStompSubscription: StompSubscription | null = null;

  constructor(private oidc: OidcSecurityService) {
  }

  connect(): void {
    if (this.client?.active) return;

    this.oidc.getAccessToken().pipe(take(1)).subscribe(token => {
      if(!token) return;

      const sub = this.jwtSub(token);
      console.warn('frontend token sub should match: ', sub);

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
            (msg: IMessage) => {
              console.warn('WS unread payload:', msg.body);
              try {
                this.unreadCountSubject.next(JSON.parse(msg.body));
              } catch {
                console.warn('Malformed ws payload', msg.body);
                //todo handle malformed payloads
              }
            }
          );
          this.client!.subscribe('/user/queue/chat.message', (msg) => {
            const dto = JSON.parse(msg.body) as MessageViewModel;
            this.chatMessageSubject.next(dto);
          })

          this.client!.subscribe('/user/queue/chat.conversation', (conv) => {
            const dto = JSON.parse(conv.body) as ConversationViewModel;
            this.chatConversationSubject.next(dto);
          })
        },
        onStompError: () => {
          console.error("STOMP ERROR");
          //todo handle broker-level errors
        },
        debug: (s) => console.log(['stomp'], s),
        onWebSocketError: (ev) => console.log('WS error', ev),
        onWebSocketClose: () => {
          this.connectedSubject.next(false);
        }
      });
      this.client.activate();
    })
  }

  disconnect(): void {
    this.unreadStompSubscription?.unsubscribe();
    this.unreadStompSubscription = null;

    this.client?.deactivate();
    this.client = null;

    this.connectedSubject.next(false);
    this.unreadCountSubject.next(null);
  }

  publish(destination: string, body: unknown): void {
    if(!this.client || !this.connectedSubject.value) return;
    this.client.publish({
      destination,
      body: JSON.stringify(body),
    })
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
