
export class CustomNotificationViewModel {

  constructor(
    public customNoteId: number,
    public enabled: boolean,
    public tagLabel: string,
    public serverId: number | null,
    public server: string | null,
    public environmentId: number | null,
    public environment: string | null,
    public experienceId: number | null,
    public experience: string | null,
    public categoryId: number | null,
    public category: string | null,
    public subcategoryId: number | null,
    public subcategory: string | null,
    public systemId: number | null,
    public system: string | null,
    public languageCode: string | null,
    public pvpStatusId: number | null,
    public pvpStatus: string | null,
    public legalityId: number | null,
    public legality: string | null,
    public groupStatusId: number | null,
    public groupStatus: string | null,
    public keywords: string | null,
    public createdAt: number,
    ) {}
}
