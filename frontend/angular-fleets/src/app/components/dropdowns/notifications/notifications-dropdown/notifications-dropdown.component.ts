import {AfterViewInit, Component, ElementRef, OnDestroy, QueryList, ViewChild, ViewChildren} from '@angular/core';
import {WsGatewayService} from "../../../../services/websocket-messaging/ws-gateway.service";
import {NotificationService} from "../../../../services/facade-services/notifications/notification.service";
import {AsyncPipe} from "@angular/common";
import {NotificationComponent} from "../notification/notification.component";
import {filter, Subject, takeUntil} from "rxjs";


@Component({
  selector: 'app-notifications-dropdown',
  standalone: true,
  templateUrl: './notifications-dropdown.component.html',
  imports: [
    AsyncPipe,
    NotificationComponent,
  ],
  styleUrl: './notifications-dropdown.component.css'
})
export class NotificationsDropdownComponent implements AfterViewInit, OnDestroy {
  private destroy$ = new Subject<void>();
  @ViewChild('scrollRoot', {static: true}) scrollRoot!: ElementRef<HTMLElement>;
  @ViewChildren('noteEl') noteEls!: QueryList<ElementRef<HTMLElement>>;

  private io?: IntersectionObserver;
  private seen = new Set<number>();

  constructor(protected ws: WsGatewayService,
              protected noteService: NotificationService) {}

  ngAfterViewInit() {
   this.noteService.openState$.pipe(takeUntil(this.destroy$),
     filter(Boolean))
     .subscribe(() => {
       setTimeout(() => this.startObserving(), 300);
     })
  }

  private startObserving() {
    const rootEl = this.scrollRoot.nativeElement;

    const isScrollable = rootEl.scrollHeight > rootEl.clientHeight;

    // if it's NOT scrollable, everything is visible -> mark all as read once
    const ids = this.noteEls
      .map(r => Number(r.nativeElement.getAttribute('data-note-id')))
      .filter(id => Number.isFinite(id));

    if (ids.length) this.noteService.setRead(ids);


    // if it IS scrollable, use IntersectionObserver with the container as root
    this.io?.disconnect();
    if(isScrollable) {
      this.io = new IntersectionObserver(this.onIntersect, {
        root: rootEl,
        threshold: 0.6,
      });
    }
    this.noteEls.forEach(r => this.io!.observe(r.nativeElement));
  }

  private onIntersect() {
    this.io = new IntersectionObserver(
      entries => {
        const newlyVisible: number[] = [];

        for(const entry of entries) {
          if (!entry.isIntersecting) continue;

          const el = entry.target as HTMLElement;
          const idStr = el.getAttribute('data-note-id');
          const id = idStr ? Number(idStr) : NaN;
          if(!Number.isFinite(id)) continue;

          if(!this.seen.has(id)) {
            this.seen.add(id);
            newlyVisible.push(id);
          }
        }

        if(newlyVisible.length) {
          this.noteService.setRead(newlyVisible)
        }
      },
      {
        root: this.scrollRoot.nativeElement,
        threshold: 0.6,
      }
    );

    const observeAll = () => this.noteEls.forEach(r => this.io!.observe(r.nativeElement));
    observeAll();

    this.noteEls.changes.subscribe(() => observeAll());
  }

  ngOnDestroy() {
    this.seen = new Set<number>();
    this.io?.disconnect();
    this.destroy$.next();
    this.destroy$.complete();
  }

  // trackById = (_: number, n: NotificationViewModel ) => n.notificationId;
}
