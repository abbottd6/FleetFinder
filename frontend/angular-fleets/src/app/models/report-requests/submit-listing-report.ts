export class SubmitListingReport {
  constructor(groupId: number, basis: number) {
    Object.assign(this, {
      groupId: groupId,
      user: null,
      reportBasis: basis,
    })
  }
}
