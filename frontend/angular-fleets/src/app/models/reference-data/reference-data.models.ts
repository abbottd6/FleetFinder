export interface ServerRegion        { serverId: number; servername: string; }
export interface GameEnvironment     { environmentId: number; environmentType: string; }
export interface GameExperience      { experienceId: number; experienceType: string; }
export interface GameplayCategory    { gameplayCategoryId: number; gameplayCategoryName: string; }
export interface GameplaySubcategory { subcategoryId: number; subcategoryName: string; gameplayCategoryId: number; gameplayCategoryName: string; }
export interface PlayStyle           { styleId: number; playStyle: string; }
export interface GroupStatus         { groupStatusId: number; groupStatus: string; }
export interface Legality            { legalityId: number; legalityStatus: string; }
export interface PvpStatus           { pvpStatusId: number; pvpStatus: string; }
export interface PlanetarySystem     { systemId: number; systemName: string; }
export interface PlanetMoonSystem    { planetId: number; planetName: string; systemId: number; systemName: string; }
