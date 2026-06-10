import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MemberChipComponent} from "../member-chip/member-chip.component";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  Observable,
  Subject,
  takeUntil
} from "rxjs";
import {
  GroupManagementUiPrefsService
} from "../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";
import {
  GroupListingFetchService
} from "../../../../services/api-services/group-listings-fetch-api/group-listing-fetch.service";
import {InviteActions, InviteWithActionInterface} from "../roster-invite-panel/invite-chip/invite-chip.component";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {RosterTabOptions} from "../roster-management.component";
import {WaitlistOptionsPanelComponent} from "./waitlist-options-panel/waitlist-options-panel.component";
import {RosterTextFieldFilterComponent} from "../roster-text-field-filter/roster-text-field-filter.component";
import {FormControl} from "@angular/forms";

export const WaitlistRosterActions = {
  MOVE_TO_ACTIVE: 'ACTIVE',
  REMOVE_WAITLIST_MEMBER: 'REMOVE',
  MESSAGE: 'MESSAGE',
} as const;

export type WaitlistRosterActions = typeof WaitlistRosterActions[keyof typeof WaitlistRosterActions];

export type WaitlistMemberWithActionInterface = {
  action: WaitlistRosterActions,
  member: GroupManagementMemberViewModel
}

export interface WaitlistMemberFilterState {
  comms: 'BOTH' | 'MIC' | 'AUDIO' | 'ANY' | 'NONE',
  terms: string | null;
}

type WaitlistMemberPredicate = (member: GroupManagementMemberViewModel) => boolean;

const WAITLIST_MEMBER_FILTER_PREDICATES: Record<string, WaitlistMemberPredicate> = {
  BOTH: (m) => m.hasMic && m.hasHeadset,
  MIC: (m) => m.hasMic,
  AUDIO: (m) => m.hasHeadset,
  NONE: (m) => !m.hasMic && !m.hasHeadset,
}

@Component({
    selector: 'app-waitlist-roster-panel',
    standalone: true,
    templateUrl: './waitlist-roster-panel.component.html',
  imports: [
    AsyncPipe,
    MemberChipComponent,
    NgForOf,
    NgIf,
    WaitlistOptionsPanelComponent,
    RosterTextFieldFilterComponent,
  ],
    styleUrl: './waitlist-roster-panel.component.css'
})
export class WaitlistRosterPanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupId!: number;

  waitlistMemberFilterState$ = new BehaviorSubject<WaitlistMemberFilterState>({
    comms: 'ANY', terms: null });

  protected waitlistFilterTermsCtrl = new FormControl<string | null>(null);

  protected waitlistMembersForDisplay$!: Observable<GroupManagementMemberViewModel[]>;

  constructor(protected managementInteract: GroupManagementInteractService,
              private mgmtUiPrefs: GroupManagementUiPrefsService,
              private listingFetch: GroupListingFetchService){}

  ngOnInit() {

    this.waitlistFilterTermsCtrl.valueChanges.pipe(
      takeUntil(this.destroy$),
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(() => {
      this.setAndFilterByTerms();
    })

    this.waitlistMembersForDisplay$ = combineLatest([
      this.managementInteract.waitlistRoster$,
      this.waitlistMemberFilterState$
    ]).pipe(
      filter(([members]) => !!members),
      map(([members, filterState]) =>
        this.filterWaitlistMembers(members.content, filterState))
    )
  }

  filterWaitlistMembers(members: GroupManagementMemberViewModel[], filterState: WaitlistMemberFilterState): GroupManagementMemberViewModel[] {
    return members.filter(member => {
      const commsMatch = filterState.comms === 'ANY' || WAITLIST_MEMBER_FILTER_PREDICATES[filterState.comms](member);
      const termsMatch = !filterState.terms ||
        member.userSummary.username.includes(filterState.terms) ||
        member.userSummary.discordUsername?.includes(filterState.terms) ||
        member.userSummary.inGameUsername?.includes(filterState.terms);

      return commsMatch && termsMatch;
    });
  }

  setAndFilterByTerms() {
    const current = this.waitlistMemberFilterState$.getValue();
    this.waitlistMemberFilterState$.next({
      ...current,
      terms: this.waitlistFilterTermsCtrl.value
    })
  }

  catchFilterStateChange(state: WaitlistMemberFilterState) {
    this.waitlistMemberFilterState$.next(state);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly RosterTabOptions = RosterTabOptions;
}
