import {Component, EventEmitter, Input, Output} from '@angular/core';
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";

@Component({
  selector: 'app-group-listing-modal',
  standalone: false,

  templateUrl: './group-listing-modal.component.html',
  styleUrl: './group-listing-modal.component.css'
})
export class GroupListingModalComponent {
  @Input() isVisible!: boolean;
  @Input() selectedListing: GroupListingViewModel | null = null;
  @Output() close = new EventEmitter<void>

  closeModal() {
    this.isVisible = false;
    this.close.emit();
  }

  closeOnBackdropClick(event: MouseEvent) {
    this.closeModal();
  }
}
