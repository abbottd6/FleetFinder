import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {GroupListingViewModel} from "../../../../models/group-listing/group-listing-view-model";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {Subject, takeUntil} from "rxjs";
import {
  GroupMembershipsInteractService
} from "../../../../services/facade-services/group-management/group-memberships-interact.service";
import {AsyncPipe, DatePipe, NgForOf, NgIf, SlicePipe} from "@angular/common";
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from "@angular/material/autocomplete";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatProgressBar} from "@angular/material/progress-bar";

@Component({
  selector: 'app-authorized-member-group-select-dropdown',
  standalone: true,
  templateUrl: './authorized-member-group-select-dropdown.component.html',
  imports: [
    MatAutocomplete,
    MatAutocompleteTrigger,
    MatFormField,
    MatInput,
    MatLabel,
    MatOption,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    SlicePipe,
    DatePipe
  ],
  styleUrl: './authorized-member-group-select-dropdown.component.css'
})

export class AuthorizedMemberGroupSelectDropdownComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupSelectCtrl!: FormControl;
  protected inviteAuthorizedGroups!: GroupListingViewModel[];
  protected noAuthorizedGroups: boolean = true;

  constructor(private membershipsInteract: GroupMembershipsInteractService){}

  ngOnInit() {
    this.membershipsInteract.getMyInviteAuthorizedMemberships().pipe(takeUntil(this.destroy$))
      .subscribe((page) => {
        this.inviteAuthorizedGroups = page.content;
        this.noAuthorizedGroups = page.content.length == 0;
      })
  }

  displaySelectedGroup(group: GroupListingViewModel | null): string {
    if(group) {
      if (group.listingTitle?.length > 32) {
        return (group.listingTitle.substring(0, 29) + '...(#' + group.groupId + ')');
      } else {
        return group.listingTitle + '(#' + group.groupId + ')';
      }
    } else {
      return '';
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
