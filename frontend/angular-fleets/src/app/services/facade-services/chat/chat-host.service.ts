import {inject, DestroyRef, Injectable} from '@angular/core';
import {BehaviorSubject, distinctUntilChanged, filter, take, takeUntil} from "rxjs";
import {AuthService} from "../../auth/auth-services/auth.service";
import {Router} from "@angular/router";
import {ChatWindowState, collapsed, expanded} from "../../../components/chat/shell-component/chat-shell.component";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {ConversationProvisionRequest} from "../../../models/chat/conversation-provision-request";
import {ChatApiService} from "../../api-services/chat-api/chat-api.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {ChatStoreService} from "./chat-store.service";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmGenericComponent} from "../../../components/pop-ups/confirm-generic/confirm-generic.component";
import { HttpErrorResponse } from '@angular/common/http';
import {UnmuteAndProvisionRequest} from "../../../models/chat/unmute-and-provision-request";

// export interface convRequest {
//   convType: string,
//   title: string,
//   recipientId: number
// }

export interface UnmuteDto {
  otherUsername: string,
  conversationId: number,
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
              private chatStoreSrv: ChatStoreService,
              private dialog: MatDialog) {
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
          this.convOnHold = response.conversationId;
          if(this.mountedSubject.value) {
            if(this.windowStateSubject.value === collapsed) {
              this.toggleWindowState();
            }
            setTimeout(() => this.chatStoreSrv.selectConversation(response.conversationId), 1000);
          } else {
            this.openChat();
          }
        },
        error: (err: any) => {
          if(err instanceof HttpErrorResponse && err.status === 409) {
            const body = err.error as {
              code: string,
              message: string,
              dto: UnmuteDto
            }

            const ask: string = 'You previously muted your conversation with this user.\n' +
                                '\nWould you like to unmute ' + body.dto.otherUsername + '?';
            const dialogRef = this.dialog.open(ConfirmGenericComponent, {
              data: {
                message: ask,
                title: null
              },
            })

            dialogRef.afterClosed().subscribe(result => {
              if(result) {
                const unmuteAndReturn = new UnmuteAndProvisionRequest(request, body.dto);
                this.chatApi.unMuteConvAndReturn(unmuteAndReturn).pipe(takeUntilDestroyed(this.chatSrvDestroyRef))
                  .subscribe({
                    next: (newResponse) => {
                      this.convOnHold = newResponse.conversationId;
                      if(this.mountedSubject.value) {
                        if(this.windowStateSubject.value === collapsed) {
                          this.toggleWindowState();
                        }
                        setTimeout(() => this.chatStoreSrv.upsertConversation(newResponse), 200);
                        setTimeout(() => this.chatStoreSrv.selectConversation(newResponse.conversationId), 500);
                      } else {
                        this.openChat();
                      }
                    }
                  });
              }
            });
          }
        }
      });
  }

  openChat() {
    this.auth.isLoggedIn$.pipe(take(1)).subscribe(isAuth => {
      if (!isAuth) {
        sessionStorage.setItem('post_login_action', 'open_chat');
        sessionStorage.setItem('post_login_url', this.router.url);
        this.auth.login();
        return;
      }

      if(this.mountedSubject.value) {
        this.toggleWindowState();
        return
      }

      this.mountedSubject.next(true);
      this.chatStoreSrv.start();
    });
  }

  closeChat() {
    this.mountedSubject.next(false);
    this.windowStateSubject.next(expanded);
    this.chatStoreSrv.stop();
    this.chatStoreSrv.clearSelectedConv();
    this.chatStoreSrv.clearActiveMessagesArr();
    this.convOnHold = null;
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
