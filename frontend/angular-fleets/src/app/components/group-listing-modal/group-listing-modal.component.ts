import {Component, DestroyRef, EventEmitter, inject, Input, OnInit, Output} from '@angular/core';
import {GroupListingViewModel} from "../../models/group-listing/group-listing-view-model";
import {AsyncPipe, DatePipe, NgClass, NgIf} from "@angular/common";
import {UserService} from "../../services/user-services/user.service";
import {MatIconModule} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {map, Observable, shareReplay, take} from "rxjs";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {ChatHostService} from "../../services/facade-services/chat/chat-host.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {environment} from "../../../environments/environment";
import {BreakpointObserver} from "@angular/cdk/layout";
import {
  ListingViewInteractionsService
} from "../../services/facade-services/listing-view-interactions/listing-view-interactions.service";

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
    AsyncPipe,
  ],
  styleUrl: './group-listing-modal.component.css'
})
export class GroupListingModalComponent implements OnInit {
  private modalDestroyRef = inject(DestroyRef)
  private breakpointObserver: BreakpointObserver = new BreakpointObserver();

  @Input() isVisible!: boolean;
  @Input() selectedListing: GroupListingViewModel | null = null;
  @Input() isBookmarked$!: Observable<boolean>;
  @Output() close = new EventEmitter<CloseValue>
  userListings: GroupListingViewModel[] = [];

  protected isHiding: boolean = false;

  constructor(private userService: UserService, protected chatHostSrv: ChatHostService,
              protected listingInteract: ListingViewInteractionsService) {

    this.userService.sessionUser$.pipe(takeUntilDestroyed(this.modalDestroyRef)).pipe(
      map(user => user?.groupListingsDto ?? [])
    ).subscribe(listings => this.userListings = listings);

  }

  ngOnInit() {
    this.userIsListingOwner();
    if(!environment.production) {
      console.log("MODAL LISTING DATA: ", this.selectedListing)
    }
  }

  routeConversation(listing: GroupListingViewModel) {
    this.isSmallDisplay$.pipe(take(1)).subscribe(small => {
      if(small) {
        this.closeModal(null, null);
      }
    })

    setTimeout(() => this.chatHostSrv.provisionConversation(listing), 300);
  }

  userIsListingOwner(): boolean {

    const selectedId = this.selectedListing?.groupId;

    if(!this.userService.sessionUser$ || !selectedId) return false;

    return this.userListings.some(
      gl => gl.groupId === selectedId
    );
  }



  closeModal(action: CloseValue['value'], group: GroupListingViewModel | null) {
    this.isHiding = true;

    setTimeout(() => {
      this.isHiding = false;
      this.isVisible = false;
      this.isVisible = false;
      history.pushState({ listingModal: false }, '');

      const emitVal: CloseValue = {
        value: action,
        group: group
      };
      this.close.emit(emitVal);
    }, 300)
  }

  isSmallDisplay$ = this.breakpointObserver
    .observe('(max-width: 991px)')
    .pipe(map(result => result.matches),
      shareReplay());

}
