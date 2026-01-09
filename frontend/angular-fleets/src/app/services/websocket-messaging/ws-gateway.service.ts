import {DestroyRef, inject, Injectable} from '@angular/core';
import {Client, StompSubscription} from "@stomp/stompjs";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {environment} from "../../../environments/environment";
import {MessageViewModel} from "../../models/chat/message-view-model";
import {ConversationViewModel} from "../../models/chat/conversation-view-model";
import {NotificationViewModel} from "../../models/NotificationViewModel";
import {AuthService} from "../auth/auth-services/auth.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

export interface UnreadSummaryDto {
  totalUnread: number;
  unreadByConv: PerConvUnreadDto[];
}

export interface PerConvUnreadDto {
  conversationId: number;
  unreadCount: number;
}

export interface NoteUnreadDto {
  count: number;
}

@Injectable({
  providedIn: 'root'
})
export class WsGatewayService {
  private wsDestroyRef = inject(DestroyRef);

  private client: Client | null = null;

  private connectedSubject = new BehaviorSubject<boolean>(false);
  public isConnected$: Observable<boolean> = this.connectedSubject.asObservable();

  private totalUnreadSubject = new BehaviorSubject<number | null>(null);
  public totalUnread$ = this.totalUnreadSubject.asObservable();

  private perConvUnreadSubject: BehaviorSubject<PerConvUnreadDto[] | null> =
    new BehaviorSubject<PerConvUnreadDto[] | null>(null);
  public perConvUnread$ = this.perConvUnreadSubject.asObservable();

  private noteUnreadSubject = new BehaviorSubject<number | null>(null);
  public notificationUnread$ = this.noteUnreadSubject.asObservable();

  private notificationsSubject = new BehaviorSubject<NotificationViewModel[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  private chatMessageSubject = new Subject<MessageViewModel>();
  public chatMessage$ = this.chatMessageSubject.asObservable();

  private chatConversationSubject = new Subject<ConversationViewModel>();
  public chatConversation$ = this.chatConversationSubject.asObservable();

  private chatUnreadStompSub: StompSubscription | null = null;

  private noteUnreadStompSub: StompSubscription | null = null;

  private notificationStompSub: StompSubscription | null = null;

  private latestAuthToken: string | null = null;

  constructor(private auth: AuthService) {
    this.auth.accessToken$.pipe(takeUntilDestroyed(this.wsDestroyRef)).subscribe(
      latest => this.latestAuthToken = latest);
  }

  subscribe<T>(destination: string, handler: (body: T) => void): StompSubscription {
    if(!this.client || !this.connectedSubject.value) {
      throw new Error('WS not connected, cannot subscribe.');
    }
    return this.client.subscribe(destination, (msg) => handler(JSON.parse(msg.body) as T));
  }

  connect(): void {
    if(this.client?.active) return;
    if(!this.latestAuthToken) return;

    this.client = new Client({
      webSocketFactory: () => new WebSocket(`${environment.wsBaseUrl}/websocket`),
      connectHeaders: {
        Authorization: `Bearer ${this.latestAuthToken}`,
      },
      reconnectDelay: 3000,
      heartbeatIncoming: 25000,
      heartbeatOutgoing: 25000,


      onConnect: () => {
        this.connectedSubject.next(true);

        this.chatUnreadStompSub = this.client!.subscribe(
          '/user/queue/chat.unread',
          (msg) => {
              const summary = JSON.parse(msg.body) as UnreadSummaryDto;
              this.totalUnreadSubject.next(summary.totalUnread);
              this.perConvUnreadSubject.next(summary.unreadByConv);
          }
        );

        this.noteUnreadStompSub = this.client!.subscribe(
          '/user/queue/system.notify_count',
          (msg) => {
            const count = JSON.parse(msg.body) as NoteUnreadDto;
            this.noteUnreadSubject.next(count.count);
          }
        )

        this.notificationStompSub = this.client!.subscribe(
          '/user/queue/system.notify',
          (msg) => {
            const note = JSON.parse(msg.body) as NotificationViewModel;
            this.incomingNotification(note);
          }
        )

        setTimeout(() => this.publish('/app/chat.total_unread', {}), 500);
      },

      onStompError: () => { console.error("STOMP ERROR") },
      debug: (s) => console.log(['stomp'], s),
      onWebSocketClose: () => {
        this.connectedSubject.next(false);
        this.chatUnreadStompSub?.unsubscribe();
        this.chatUnreadStompSub = null;

        this.noteUnreadStompSub?.unsubscribe();
        this.noteUnreadStompSub = null;

        this.notificationStompSub?.unsubscribe();
        this.notificationStompSub = null;

      }
    });

    this.client.activate();
  }

  disconnect(): void {
    this.chatUnreadStompSub?.unsubscribe();
    this.chatUnreadStompSub = null;

    this.noteUnreadStompSub?.unsubscribe();
    this.noteUnreadStompSub = null;

    this.notificationStompSub?.unsubscribe();
    this.notificationStompSub = null;

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

  incomingNotification(note: NotificationViewModel) {
    const current = this.notificationsSubject.value;
     const exists = current.some(n  =>
       (n.notificationId && n.notificationId === note.notificationId)
     );

     if(exists) return;

     this.notificationsSubject.next([note, ...current]);
  }

  setNotesArray(notes: NotificationViewModel[]) {
    this.notificationsSubject.next(notes);
  }

  getNotesArray() {
    return this.notificationsSubject.value
  }

  setNotesUnread(count: number): void {
    this.noteUnreadSubject.next(count);
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
