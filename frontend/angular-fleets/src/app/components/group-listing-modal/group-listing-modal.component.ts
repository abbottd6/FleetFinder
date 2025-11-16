import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {DatePipe, NgClass, NgIf} from "@angular/common";
import {UserService} from "../../services/user-services/user.service";
import {MatIconModule} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {map, Observable} from "rxjs";
import {PrivateUser} from "../../models/private-user/private-user";


@Component({
  selector: 'app-group-listing-modal',
  standalone: true,
  templateUrl: './group-listing-modal.component.html',
  imports: [
    NgClass,
    DatePipe,
    NgIf,
    MatIconModule,
    RouterLink
  ],
  styleUrl: './group-listing-modal.component.css'
})
export class GroupListingModalComponent implements OnInit {
  @Input() isVisible!: boolean;
  @Input() selectedListing: GroupListingViewModel | null = null;
  @Output() close = new EventEmitter<void>
  localUser$: Observable<PrivateUser>;
  userListings: GroupListingViewModel[] = [];
  userService = new UserService();

  constructor(userService: UserService) {
    this.userService = userService;
    this.localUser$ = this.userService.localUser$;

    this.localUser$.pipe(
      map(user => user.groupListingsDto ?? [])
    )
      .subscribe(listings => this.userListings = listings);

  }

  ngOnInit() {
    this.userIsListingOwner();
    console.log("MODAL LISTING DATA: ", this.selectedListing)
  }

  userIsListingOwner(): boolean {
    const selectedId = this.selectedListing?.groupId;

    if(!this.localUser$ || !selectedId) return false;

    return this.userListings.some(
      gl => gl.groupId === selectedId
    );
  }

  closeModal() {
    this.isVisible = false;
    this.close.emit();
  }

  closeOnBackdropClick(event: MouseEvent) {
    this.closeModal();
  }
}
