export class CrewTemplateViewModel {
  constructor(
    public templateId: number,
    public templateLabel: string,
    public templateCategory: string,
    public ownerId: number,
    public ownerUsername: string,
    public lastUsedAt: Date
  ){}
}
