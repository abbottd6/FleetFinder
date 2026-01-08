import {ElementRef, Injectable} from '@angular/core';
import {MatMenuTrigger} from "@angular/material/menu";

@Injectable({
  providedIn: 'root'
})
export class ChatOptionsMenuService {
  private trigger?: MatMenuTrigger;
  private anchorEl?: ElementRef<HTMLElement>;

  longPressTimer: any;
  private readonly LONG_PRESS_MS = 500;

  public longPressTriggered = false;

  constructor() { }

  registerMenu(trigger: MatMenuTrigger, anchorEl: ElementRef<HTMLElement>): void {
    this.trigger = trigger;
    this.anchorEl = anchorEl;
  }

  openContextMenu(event: MouseEvent) {
    event.preventDefault();

    this.openMenuAt(event.clientX, event.clientY);
  }

  openMenuAt(x: number, y: number) {
    if(!this.anchorEl || this.trigger == undefined) return;
    const el = this.anchorEl.nativeElement;

    el.style.left = `${x}px`;
    el.style.top = `${y}px`;
    queueMicrotask(() => {
      if(!this.anchorEl || !this.trigger) return;
      this.trigger?.openMenu()
    });
  }

  onTouchStart(event: TouchEvent) {
    if(event.touches.length !== 1) return;
    this.longPressTriggered = false;

    const touch = event.touches[0];
    this.longPressTimer = setTimeout(() => {
      this.longPressTriggered = true;
      if(this.longPressTimer >= this.LONG_PRESS_MS) {
        event.preventDefault();
      }
      this.openMenuAt(touch.clientX, touch.clientY);
    }, this.LONG_PRESS_MS);
  }

  onTouchEnd(event: TouchEvent) {
    if(this.longPressTriggered) {
      event.preventDefault();
    }
    clearTimeout(this.longPressTimer);
    this.longPressTriggered = false;
  }
}
