import {DestroyRef, inject, Injectable} from '@angular/core';
import {
  InvitePanelFilterState
} from "../../../../components/group-management-page/roster-management/roster-invite-panel/roster-invite-panel.component";
import {DropListOrientation} from "@angular/cdk/drag-drop";
import {
  ActiveMemberFilterState
} from "../../../../components/group-management-page/roster-management/active-roster-panel/active-roster-panel.component";
import {
  WaitlistMemberFilterState
} from "../../../../components/group-management-page/roster-management/waitlist-roster-panel/waitlist-roster-panel.component";

export const GRP_MGMT_PREFS_KEY = 'ff_grp_mgmt_ui_prefs';

export const InviteDirection = {
 OFFER: 'OFFER',
 REQUEST: 'REQUEST',
 BOTH: 'BOTH'
} as const;
export type InviteDirection = typeof InviteDirection[keyof typeof InviteDirection];

export const InviteStatus = {
  PENDING: 'PENDING',
  ACTIONED: 'ACTIONED',
  BOTH: 'BOTH'
} as const;
export type InviteStatus = typeof InviteStatus[keyof typeof InviteStatus];

export interface InvitePanelFilterPrefs {
  direction: InviteDirection,
  status: InviteStatus
}

export interface ActiveRosterFilterPrefs {
  roleStatus: 'ASSIGNED' | 'UNASSIGNED' | 'BOTH',
  rsvpStatus: 'CONFIRMED' | 'PENDING' | 'BOTH',
  comms: 'BOTH' | 'MIC' | 'AUDIO' | 'ANY' | 'NONE'
}

export interface WaitlistRosterFilterPrefs {
  comms: 'BOTH' | 'MIC' | 'AUDIO' | 'ANY' | 'NONE'
}

export interface GroupCompositionPreferences {
  rootDropListOrientation: DropListOrientation
}

export interface ManagementPageUiPrefs {
  activeRosterFilters: ActiveRosterFilterPrefs,
  invitePanelFilters: InvitePanelFilterPrefs,
  waitlistRosterFilters: WaitlistRosterFilterPrefs,
  groupCompositionPrefs: GroupCompositionPreferences
}

@Injectable({
  providedIn: 'root'
})
export class GroupManagementUiPrefsService {
  private destroyRef = inject(DestroyRef);

  groupManagementUiPrefs!: ManagementPageUiPrefs;

  constructor() {
    this.groupManagementUiPrefs = this.loadGroupManagementUiPrefs();
  }

  get storedInviteFilters() {
    return this.groupManagementUiPrefs.invitePanelFilters;
  }

  get storedActiveRosterFilters() {
    return this.groupManagementUiPrefs.activeRosterFilters;
  }

  get storedWaitlistRosterFilters() {
    return this.groupManagementUiPrefs.waitlistRosterFilters;
  }

  public loadGroupManagementUiPrefs(): ManagementPageUiPrefs {
    try {
      const localPrefs = localStorage.getItem(GRP_MGMT_PREFS_KEY);
      if(!localPrefs) {
        return {
          activeRosterFilters: {
            roleStatus: 'BOTH',
            rsvpStatus: 'BOTH',
            comms: 'ANY',
          },
          invitePanelFilters: {
            direction: InviteDirection.BOTH,
            status: InviteStatus.PENDING,
          },
          waitlistRosterFilters: {
            comms: 'ANY',
          },
          groupCompositionPrefs: {
            rootDropListOrientation: 'horizontal',
          }
        }
      }
      const parsed = JSON.parse(localPrefs) as Partial<ManagementPageUiPrefs>;
      return {
        activeRosterFilters: {
          roleStatus: parsed.activeRosterFilters?.roleStatus ?? 'BOTH',
          rsvpStatus: parsed.activeRosterFilters?.rsvpStatus ?? 'BOTH',
          comms: parsed.activeRosterFilters?.comms ?? 'ANY',
        },
        invitePanelFilters: {
          direction: parsed.invitePanelFilters?.direction as InviteDirection ?? InviteDirection.BOTH,
          status: parsed.invitePanelFilters?.status as InviteStatus ?? InviteStatus.BOTH
        },
        waitlistRosterFilters: {
          comms: parsed.waitlistRosterFilters?.comms ?? 'ANY',
        },
        groupCompositionPrefs: {
          rootDropListOrientation: (parsed.groupCompositionPrefs?.rootDropListOrientation ?? 'horizontal') as DropListOrientation,
        },
      }
    } catch {
      localStorage.removeItem(GRP_MGMT_PREFS_KEY);
      return {
        activeRosterFilters: {
          roleStatus: 'BOTH',
          rsvpStatus: 'BOTH',
          comms: 'ANY',
        },
        invitePanelFilters: {
          direction: InviteDirection.BOTH,
          status: InviteStatus.PENDING,
        },
        waitlistRosterFilters: {
          comms: 'ANY',
        },
        groupCompositionPrefs: {
          rootDropListOrientation: 'horizontal' as DropListOrientation
        }
      }
    }
  }

  saveActiveRosterUiPrefs(state: ActiveMemberFilterState) {
    this.groupManagementUiPrefs.activeRosterFilters.roleStatus = state.roleStatus;
    this.groupManagementUiPrefs.activeRosterFilters.rsvpStatus = state.rsvpStatus;
    this.groupManagementUiPrefs.activeRosterFilters.comms = state.comms;

    this.saveUiPrefs(this.groupManagementUiPrefs);
  }

  saveInviteUiPrefs(state: InvitePanelFilterState) {
    this.groupManagementUiPrefs.invitePanelFilters.direction = state.direction;
    this.groupManagementUiPrefs.invitePanelFilters.status = state.status;

    this.saveUiPrefs(this.groupManagementUiPrefs);
  }

  saveWaitlistUiPrefs(state: WaitlistMemberFilterState) {
    this.groupManagementUiPrefs.waitlistRosterFilters.comms = state.comms;

    this.saveUiPrefs(this.groupManagementUiPrefs);
  }

  saveGroupCompUiPrefs(state: GroupCompositionPreferences) {
    this.groupManagementUiPrefs.groupCompositionPrefs = state;

    this.saveUiPrefs(this.groupManagementUiPrefs);
  }

  get getRootDropListOrientation() {
    return this.groupManagementUiPrefs.groupCompositionPrefs.rootDropListOrientation;
  }

  setRootDropListOrientation(newOrientation: DropListOrientation) {
    this.groupManagementUiPrefs.groupCompositionPrefs.rootDropListOrientation = newOrientation;

    this.saveGroupCompUiPrefs(this.groupManagementUiPrefs.groupCompositionPrefs);
  }

  private saveUiPrefs(prefs: ManagementPageUiPrefs) {
    localStorage.setItem(GRP_MGMT_PREFS_KEY, JSON.stringify({
      ...prefs
    }))
  }
}
