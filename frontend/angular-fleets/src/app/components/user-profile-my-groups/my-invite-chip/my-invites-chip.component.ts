import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {
  GroupInviteViewModel
} from "../../../models/group-management-models/view-models/group-membership/group-invite-view-model";
import {DatePipe, NgIf, SlicePipe, TitleCasePipe} from "@angular/common";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {GroupListingViewModel} from "../../../models/group-listing/group-listing-view-model";
import {
  ListingViewInteractionsService
} from "../../../services/facade-services/listing-view-interactions/listing-view-interactions.service";
import {ChatHostService} from "../../../services/facade-services/chat/chat-host.service";
import {Router} from "@angular/router";
import {
  InviteActions, InviteWithActionInterface
} from "../../group-management-page/roster-management/roster-invite-panel/invite-chip/invite-chip.component";

export interface MemberInviteActionInterface {
  action: InviteActions,
  invite: GroupInviteViewModel
}

@Component({
  selector: 'app-my-invites-chip',
  standalone: true,
  templateUrl: './my-invites-chip.component.html',
  imports: [
    DatePipe,
    MatIcon,
    NgIf,
    SlicePipe,
    TitleCasePipe
  ],
  styleUrl: './my-invites-chip.component.css'
})
export class MyInvitesChipComponent implements OnInit {

  @Input() invite!: GroupInviteViewModel;
  @Output() emitListing = new EventEmitter<GroupListingViewModel>();
  @Output() inviteActionEmitter = new EventEmitter<MemberInviteActionInterface>();

  protected listingDetails!: GroupListingViewModel;

  constructor(protected listingInteract: ListingViewInteractionsService,
              protected chatHostSrv: ChatHostService,
              private router: Router){}

  ngOnInit() {
    this.listingDetails = this.invite.listingDetails;
  }

  showListingModal() {
    this.emitListing.emit(this.listingDetails);
  }

  emitInviteAction(action: InviteActions) {
    const inviteWithAction: MemberInviteActionInterface = {action: action, invite: this.invite};
    this.inviteActionEmitter.emit(inviteWithAction);
  }

  protected readonly InviteActions = InviteActions;
}
