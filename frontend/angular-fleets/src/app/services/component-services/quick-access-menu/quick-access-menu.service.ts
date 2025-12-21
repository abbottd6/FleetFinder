import {ElementRef, Injectable} from '@angular/core';
import {MatMenuTrigger} from "@angular/material/menu";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {
  ListingViewInteractionsService
} from "../../facade-services/listing-view-interactions/listing-view-interactions.service";

@Injectable({
  providedIn: 'root'
})
export class QuickAccessMenuService {
  private trigger?: MatMenuTrigger;
  private anchorEl?: ElementRef<HTMLElement>;

  longPressTimer: any;
  private readonly LONG_PRESS_MS = 500;

  public longPressTriggered = false;

  constructor(private listingInteract: ListingViewInteractionsService) { }

  /* ------------------------------------------ QUICK ACCESS MENU ----------------------------------------------------*/

  registerMenu(trigger: MatMenuTrigger, anchorEl: ElementRef<HTMLElement>): void {
    this.trigger = trigger;
    this.anchorEl = anchorEl;
  }

  openContextMenu(event: MouseEvent, row: GroupListingViewModel) {
    event.preventDefault();
    this.listingInteract.setSelectedListing(row);

    this.openMenuAt(event.clientX, event.clientY);
  }

  openMenuAt(x: number, y: number) {
    if (!this.anchorEl || this.trigger == undefined) return;
    const el = this.anchorEl.nativeElement;

    el.style.left = `${x}px`;
    el.style.top = `${y}px`;
    queueMicrotask(() => this.trigger?.openMenu());
  }

  onTouchStart(event: TouchEvent, row: GroupListingViewModel) {
    if(event.touches.length !== 1) return;
    this.listingInteract.setSelectedListing(row)
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
