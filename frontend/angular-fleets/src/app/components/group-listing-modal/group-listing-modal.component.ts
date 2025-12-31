import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {AsyncPipe, DatePipe, NgClass, NgIf} from "@angular/common";
import {UserService} from "../../services/user-services/user.service";
import {MatIconModule} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {map, Observable} from "rxjs";
import {PrivateUser} from "../../models/private-user/private-user";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";

export interface CloseValue {
  value: 'hide' | 'bookmark' | 'unbookmark' | 'report' | 'delete' | null,
  group: GroupListingViewModel | null
}

@Component({
  selector: 'app-group-listing-modal',
  standalone: true,
  templateUrl: './group-listing-modal.component.html',
  imports: [
    NgClass,
    DatePipe,
    NgIf,
    MatIconModule,
    RouterLink,
    MatMenuTrigger,
    MatMenu,
    MatMenuItem,
    AsyncPipe
  ],
  styleUrl: './group-listing-modal.component.css'
})
export class GroupListingModalComponent implements OnInit {
  @Input() isVisible!: boolean;
  @Input() selectedListing: GroupListingViewModel | null = null;
  @Input() isBookmarked$!: Observable<boolean>;
  @Output() close = new EventEmitter<CloseValue>
  localUser$: Observable<PrivateUser>;
  userListings: GroupListingViewModel[] = [];

  constructor(private userService: UserService, protected chatHostSrv: ChatHostService) {
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

  closeModal(action: CloseValue['value'], group: GroupListingViewModel | null) {
    this.isVisible = false;
    const emitVal: CloseValue = {
      value: action,
      group: group
    };
    this.close.emit(emitVal);
  }

}
