export class ModIssueViewModel {

  constructor(
    public issueId: number,
    public groupId: number,
    public username: string,
    public userId: number,
    public reportTotalCount: number,
    public spamCount: number,
    public hateSpeechCount: number,
    public nsfwCount: number,
    public scamCount: number,
    public offTopicCount: number,
    public trollCount: number,
    public doxxCount: number,
    public cheatCount: number,
    public otherCount: number,
    public firstReportTs: Date,
    public lastReportTs: Date,
    public status: string
  ){}
}
