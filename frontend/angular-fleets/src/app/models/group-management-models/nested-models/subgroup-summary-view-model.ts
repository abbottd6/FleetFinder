export class SubgroupSummaryViewModel {
  constructor(
    public subgroupId: number,
    public subgroupLabel: string,
    public subgroupNotes: string,
    public parentSubgroupId: number,
    public parentSubgroupLabel: string | null,
    // public intendedSubgroupSize: number,
    public createdAt: Date
  ){}
}
