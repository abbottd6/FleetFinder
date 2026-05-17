import {DestroyRef, inject, Injectable} from '@angular/core';
import {
  InvitePanelFilterState
} from "../../../../components/group-management-page/roster-management/roster-invite-panel/roster-invite-panel.component";
import {DropListOrientation} from "@angular/cdk/drag-drop";

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

export interface InvitePanelOptions {
  direction: InviteDirection,
  status: InviteStatus
}

export interface GroupCompositionPreferences {
  rootDropListOrientation: DropListOrientation
}

export interface ManagementPageUiPrefs {
  invitePanelOptions: InvitePanelOptions,
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
    return this.groupManagementUiPrefs.invitePanelOptions;
  }

  public loadGroupManagementUiPrefs() {
    try {
      const localPrefs = localStorage.getItem(GRP_MGMT_PREFS_KEY);
      if(!localPrefs) {
        return {
          invitePanelOptions: {
            direction: InviteDirection.BOTH,
            status: InviteStatus.PENDING,
          },
          groupCompositionPrefs: {
            rootDropListOrientation: 'horizontal' as DropListOrientation,
          }
        }
      }
      const parsed = JSON.parse(localPrefs) as Partial<ManagementPageUiPrefs>;
      return {
        invitePanelOptions: {
          direction: parsed.invitePanelOptions?.direction as InviteDirection ?? InviteDirection.BOTH,
          status: parsed.invitePanelOptions?.status as InviteStatus ?? InviteStatus.BOTH
        },
        groupCompositionPrefs: {
          rootDropListOrientation: (parsed.groupCompositionPrefs?.rootDropListOrientation ?? 'horizontal') as DropListOrientation,
        },
      }
    } catch {
      localStorage.removeItem(GRP_MGMT_PREFS_KEY);
      return {
        invitePanelOptions: {
          direction: InviteDirection.BOTH,
          status: InviteStatus.PENDING,
        },
        groupCompositionPrefs: {
          rootDropListOrientation: 'horizontal' as DropListOrientation
        }
      }
    }
  }

  saveInviteUiPrefs(state: InvitePanelFilterState) {
    this.groupManagementUiPrefs.invitePanelOptions.direction = state.direction;
    this.groupManagementUiPrefs.invitePanelOptions.status = state.status;

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
