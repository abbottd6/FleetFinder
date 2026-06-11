import {GroupManagementMemberViewModel} from "../view-models/group-membership/group-management-member-view-model";

export class ConvertWaitlistMemberInvite {
  constructor(member: GroupManagementMemberViewModel) {
    Object.assign(this, {
      listingId: member.listingId,
      recipientSummary: member.userSummary,
      memberStatus: 'ACTIVE',
      roleSummary: member.memberPosition?.roleSummary,
      inviteMessage: 'A position on the active roster has opened up.',
      convertFromWaitlistMember: true,
      expiresAt: null,
    })
  }
}
