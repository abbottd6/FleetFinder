import {inject, DestroyRef, Injectable} from '@angular/core';
import {BehaviorSubject, distinctUntilChanged, filter, take, takeUntil} from "rxjs";
import {AuthService} from "../../auth/auth-services/auth.service";
import {Router} from "@angular/router";
import {ChatWindowState, collapsed, expanded} from "../../../components/chat/shell-component/chat-shell.component";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {ConversationProvisionRequest} from "../../../models/chat/conversation-provision-request";
import {ChatApiService} from "../../api-services/chat-api/chat-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {WsGatewayService} from "../../websocket-messaging/ws-gateway.service";
import {ChatStoreService} from "./chat-store.service";

export interface convRequest {
  convType: string,
  title: string,
  recipientId: number
}

@Injectable({
  providedIn: 'root'
})
export class ChatHostService {
  private chatSrvDestroyRef = inject(DestroyRef);

  private mountedSubject = new BehaviorSubject<boolean>(false);
  mounted$ = this.mountedSubject.asObservable();

  private openSubject = new BehaviorSubject<boolean>(false);
  open$ = this.openSubject.asObservable();

  public convOnHold: number | null = null;

  private windowStateSubject = new BehaviorSubject<ChatWindowState>(expanded);
  public windowState$ = this.windowStateSubject.asObservable();

  constructor(private auth: AuthService,
              private router: Router,
              private chatApi: ChatApiService,
              private chatStoreSrv: ChatStoreService) {
    this.auth.isLoggedIn$
      .pipe(distinctUntilChanged(), filter(Boolean))
      .subscribe(() => {
        const pendingAction = sessionStorage.getItem('post_login_action');
        if(pendingAction === 'open_chat') {
          sessionStorage.removeItem('post_login_action');
          this.openChat();
        }
      })
  }

  provisionConversation(listing: GroupListingViewModel) {
    const request = new ConversationProvisionRequest(
      listing.listingTitle, listing.userId);

    this.auth.isLoggedIn$.pipe(take(1)).subscribe(isAuth => {
      if (!isAuth) {
        sessionStorage.setItem('post_login_action', 'provision_conversation');
        sessionStorage.setItem('chat_sequence', JSON.stringify({request}));
        sessionStorage.setItem('post_login_url', this.router.url);
        this.auth.login();
        return
      }
    })
    this.chatApi.conversationProvision(request).pipe(takeUntilDestroyed(this.chatSrvDestroyRef))
      .subscribe({
        next: (response) => {
          console.log("Response convID: ", response.conversationId)
          this.convOnHold = response.conversationId;
          this.openChat();
        }
      })
  }

  openChat() {
    this.auth.isLoggedIn$.pipe(take(1)).subscribe(isAuth => {
      if (!isAuth) {
        sessionStorage.setItem('post_login_action', 'open_chat');
        sessionStorage.setItem('post_login_url', this.router.url);
        this.auth.login();
        return;
      }

      this.mountedSubject.next(true);
      this.chatStoreSrv.start();
    });
  }

  closeChat() {
    this.mountedSubject.next(false);
    this.chatStoreSrv.stop();
  }

  toggleChat() {
    const isOpen = this.openSubject.value;
    if (isOpen) {
      this.mountedSubject.next(false);
    }
    else this.openChat();
  }

  toggleWindowState() {
    const next = this.windowStateSubject.value === expanded ? collapsed : expanded;

    this.windowStateSubject.next(next);
    return next;
  }

  authCheckOrRedirect() {
    this.auth.isLoggedIn$.pipe(take(1)).subscribe(isAuth => {
      if (!isAuth) {
        sessionStorage.setItem('post_login_url', this.router.url);
        this.auth.login();
        return;
      }
    });
  }
}
