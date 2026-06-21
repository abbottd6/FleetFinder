import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {
  BehaviorSubject,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  filter,
  Observable,
  Subject,
  takeUntil
} from "rxjs";
import {
  GroupManagementInteractService
} from "../../../../services/facade-services/group-management/group-management-interact.service";
import {ActiveRosterOptionsPanelComponent} from "./active-roster-options-panel/active-roster-options-panel.component";
import {AsyncPipe, NgIf} from "@angular/common";
import {MemberChipComponent} from "../member-chip/member-chip.component";
import {RosterTabOptions} from "../roster-management.component";
import {
  GroupManagementMemberViewModel
} from "../../../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {CdkDrag, CdkDropList} from "@angular/cdk/drag-drop";
import {
  DropListRegistryService
} from "../../../../services/facade-services/group-management/drop-list-registry.service";
import {
  GroupCompositionInteractService
} from "../../../../services/facade-services/group-management/group-composition-interact.service";
import {RosterTextFieldFilterComponent} from "../roster-text-field-filter/roster-text-field-filter.component";
import {FormControl} from "@angular/forms";
import {map} from "rxjs/operators";
import {
  MgmtMemberQuickAccessMenuService
} from "../../../../services/component-services/group-management-quick-access-menus/mgmt-member-quick-access-menu.service";
import {
  GroupManagementUiPrefsService
} from "../../../../services/facade-services/group-management/group-management-ui-prefs/group-management-ui-prefs.service";

export interface ActiveMemberFilterState {
  roleStatus: 'ASSIGNED' | 'UNASSIGNED' | 'BOTH',
  rsvpStatus: 'CONFIRMED' | 'PENDING' | 'BOTH',
  comms: 'BOTH' | 'MIC' | 'AUDIO' | 'ANY' | 'NONE',
  terms: string | null;
}

type ActiveMemberPredicate = (member: GroupManagementMemberViewModel) => boolean;

const ACTIVE_MEMBER_FILTER_PREDICATES: Record<string, ActiveMemberPredicate> = {
  ASSIGNED: (m) => m.memberPosition !== null,
  UNASSIGNED: (m) => m.memberPosition === null,
  BOTH: (m) => m.hasMic && m.hasHeadset,
  MIC: (m) => m.hasMic,
  AUDIO: (m) => m.hasHeadset,
  NONE: (m) => !m.hasMic && !m.hasHeadset,
  CONFIRMED: (m) => m.rsvpStatus !== null && m.rsvpStatus === 'CONFIRMED',
  PENDING: (m) => m.rsvpStatus !== null && m.rsvpStatus === 'PENDING',
};

@Component({
  selector: 'app-active-roster-panel',
  standalone: true,
  templateUrl: './active-roster-panel.component.html',
  imports: [
    ActiveRosterOptionsPanelComponent,
    AsyncPipe,
    MemberChipComponent,
    CdkDropList,
    NgIf,
    CdkDrag,
    RosterTextFieldFilterComponent,
  ],
  styleUrl: './active-roster-panel.component.css'
})
export class ActiveRosterPanelComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  @Input() groupId!: number;

  protected memberFilterTermsCtrl = new FormControl<string | null>(null);

  activeMemberFilterState$ = new BehaviorSubject<ActiveMemberFilterState>({
    roleStatus: 'BOTH', rsvpStatus: 'BOTH', comms: 'ANY', terms: null
  })

  activeRosterForDisplay$!: Observable<GroupManagementMemberViewModel[]>;

  constructor(protected managementInteract: GroupManagementInteractService,
              protected dropListRegistry: DropListRegistryService,
              protected subgroupInteract: GroupCompositionInteractService,
              private mgmtUiPrefs: GroupManagementUiPrefsService){}

  ngOnInit() {
    this.activeMemberFilterState$.next({
      ...this.mgmtUiPrefs.storedActiveRosterFilters,
      terms: null
    });

    this.memberFilterTermsCtrl.valueChanges.pipe(
      takeUntil(this.destroy$),
      debounceTime(500),
      distinctUntilChanged()
    ).subscribe(() => {
      this.setAndFilterByTerms();
    })

    this.activeRosterForDisplay$ = combineLatest([
      this.managementInteract.activeRoster$,
      this.activeMemberFilterState$
    ]).pipe(
      takeUntil(this.destroy$),
      filter(([members]) => !!members),
      map(([members, filterState]) =>
        this.filterActiveMembers(members.content, filterState))
    )
  }

  filterActiveMembers(members: GroupManagementMemberViewModel[], filterState: ActiveMemberFilterState): GroupManagementMemberViewModel[] {
    return members.filter(member => {
      const roleMatch = filterState.roleStatus === 'BOTH' || ACTIVE_MEMBER_FILTER_PREDICATES[filterState.roleStatus](member);
      const rsvpMatch = filterState.rsvpStatus === 'BOTH' || ACTIVE_MEMBER_FILTER_PREDICATES[filterState.rsvpStatus](member);
      const commsMatch = filterState.comms === 'ANY' || ACTIVE_MEMBER_FILTER_PREDICATES[filterState.comms](member);
      const termsMatch = !filterState.terms ||
        member.userSummary.username.includes(filterState.terms) ||
        member.userSummary.discordUsername?.includes(filterState.terms) ||
        member.userSummary.inGameUsername?.includes(filterState.terms);

      return roleMatch && rsvpMatch && commsMatch && termsMatch;
    });
  }

  setAndFilterByTerms() {
    const current = this.activeMemberFilterState$.getValue();
    this.activeMemberFilterState$.next({
      ...current,
      terms: this.memberFilterTermsCtrl.value
    })
  }

  catchFilterStateChange(state: ActiveMemberFilterState) {
    this.activeMemberFilterState$.next(state);
    this.mgmtUiPrefs.saveActiveRosterUiPrefs(state);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly RosterTabOptions = RosterTabOptions;
}
