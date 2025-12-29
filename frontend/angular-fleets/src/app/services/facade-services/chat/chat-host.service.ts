import {inject, DestroyRef, Injectable} from '@angular/core';
import {BehaviorSubject, distinctUntilChanged, filter, take} from "rxjs";
import {AuthService} from "../../auth/auth-services/auth.service";
import {Router} from "@angular/router";
import {ChatWindowState, collapsed, expanded} from "../../../components/chat/shell-component/chat-shell.component";

@Injectable({
  providedIn: 'root'
})
export class ChatHostService {
  private chatSrvDestroyRef = inject(DestroyRef);

  private mountedSubject = new BehaviorSubject<boolean>(false);
  mounted$ = this.mountedSubject.asObservable();

  private openSubject = new BehaviorSubject<boolean>(false);
  open$ = this.openSubject.asObservable();

  private windowStateSubject = new BehaviorSubject<ChatWindowState>(expanded);
  public windowState$ = this.windowStateSubject.asObservable();

  private initialized = false;


  constructor(private auth: AuthService, private router: Router) {
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

  openChat() {
    this.auth.isLoggedIn$.pipe(take(1)).subscribe(isAuth => {
      if (!isAuth) {
        sessionStorage.setItem('post_login_action', 'open_chat');
        sessionStorage.setItem('post_login_url', this.router.url);
        this.auth.login();
        return;
      }

      this.mountedSubject.next(true);
      this.openSubject.next(true);
      if(!this.initialized) {
        // this.initialized = true;
        // this.chatData.init();
      }
    });
  }

  closeChat() {
    this.openSubject.next(false);
    this.mountedSubject.next(false);
  }

  toggleChat() {
    const isOpen = this.openSubject.value;
    if (isOpen) {
      this.openSubject.next(false);
      this.mountedSubject.next(false);
    }
    else this.openChat();
  }

  toggleWindowState() {
    const next = this.windowStateSubject.value === expanded ? collapsed : expanded;

    this.windowStateSubject.next(next);
  }
}
