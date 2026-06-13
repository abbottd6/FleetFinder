import {RoleClassSummaryViewModel} from "../../nested-models/role-class-summary-view-model";
import {GroupManagementMemberViewModel} from "../group-membership/group-management-member-view-model";

export class GroupCompCrewPositionViewModel {
  readonly entityType = 'position' as const;

  constructor(
    public positionId: number,
    public groupId: number,
    public groupTitle: string,
    public rootSubgroupId: number,
    public subgroupId: number,
    public subgroupLabel: string,
    public sortOrder: number,
    public groupRole: RoleClassSummaryViewModel,
    public positionNote: string,
    public assignedMember: GroupManagementMemberViewModel | null,
    public filledAt: Date,
    public vacatedAt: Date,
    public createdAt: Date
  ){}
}
