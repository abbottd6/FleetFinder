import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {map, Observable, of, tap} from "rxjs";
import {environment} from '../../../../environments/environment';
import {
  GameEnvironment,
  GameExperience,
  GameplayCategory,
  GameplaySubcategory,
  GroupStatus,
  Legality,
  PlanetarySystem,
  PlanetMoonSystem,
  PlayStyle,
  PvpStatus,
  ServerRegion
} from "../../../models/reference-data/reference-data.models";

@Injectable({
  providedIn: 'root'
})

// Service for accessing lookup table data from database so that it can be used in forms
export class LookupService {

  private baseUrl = `${environment.apiBaseUrl}`;

  //caches
  private serverRegionsCache: ServerRegion[] | null = null;
  private gameEnvironmentsCache: GameEnvironment[] | null = null;
  private gameExperiencesCache: GameExperience[] | null = null;
  private playStylesCache: PlayStyle[] | null = null;
  private legalitiesCache: Legality[] | null = null;
  private groupStatusesCache: GroupStatus[] | null = null;
  private gameplayCategoriesCache: GameplayCategory[] | null = null;
  private gameplaySubCategoriesCache: GameplaySubcategory[] | null = null;
  private pvpStatusesCache: PvpStatus[] | null = null;
  private planetarySystemCache: PlanetarySystem[] | null = null;
  private planetMoonSystemsCache: PlanetMoonSystem[] | null = null;

  constructor(private http: HttpClient) { }

  //generic helper caching method
  private getCached<T>(cache: T[] | null, endpoint: string, setCache: (data: T[]) => void): Observable<T[]> {
    if (cache) {
      return of(cache)
    } else {
      return this.http.get<T[]>(`${this.baseUrl}${endpoint}`).pipe(
        tap(data => setCache(data))
      );
    }
  }

  getServerRegions(): Observable<ServerRegion[]> {
    return this.getCached(this.serverRegionsCache,
      `/lookup/server-regions`, data => this.serverRegionsCache = data);
  }

  getGameEnvironments(): Observable<GameEnvironment[]> {
    return this.getCached(this.gameEnvironmentsCache,
      `/lookup/game-environments`, data => this.gameEnvironmentsCache = data);
  }

  getGameExperiences(): Observable<GameExperience[]> {
    return this.getCached(this.gameExperiencesCache,
      `/lookup/game-experiences`, data => this.gameExperiencesCache = data);
  }

  getPlayStyles(): Observable<PlayStyle[]> {
    return this.getCached(this.playStylesCache,
      `/lookup/play-styles`, data => this.playStylesCache = data);
  }

  getLegalities(): Observable<Legality[]> {
    return this.getCached(this.legalitiesCache,
      `/lookup/legalities`, data => this.legalitiesCache = data);
  }

  getGroupStatuses(): Observable<GroupStatus[]> {
    return this.getCached(this.groupStatusesCache,
      `/lookup/group-statuses`, data => this.groupStatusesCache = data);
  }

  getGameplayCategories(): Observable<GameplayCategory[]> {
    return this.getCached(this.gameplayCategoriesCache,
      `/lookup/gameplay-categories`, data => this.gameplayCategoriesCache = data);
  }

  getGameplaySubcategories(): Observable<GameplaySubcategory[]> {
    return this.getCached(this.gameplaySubCategoriesCache,
      `/lookup/gameplay-subcategories`, data => this.gameplaySubCategoriesCache = data);
  }

  getPvpStatuses(): Observable<PvpStatus[]> {
    return this.getCached(this.pvpStatusesCache,
      `/lookup/pvp-statuses`, data => this.pvpStatusesCache = data);
  }

  //Broader systems: Stanton, Pyro, etc.
  getPlanetarySystems(): Observable<PlanetarySystem[]> {
    return this.getCached(this.planetarySystemCache,
      `/lookup/planetary-systems`, data => this.planetarySystemCache = data);
  }

  //Subsystems: Hurston, Microtech, etc.
  getPlanetMoonSystems(): Observable<PlanetMoonSystem[]> {
    return this.getCached(this.planetMoonSystemsCache,
      `/lookup/planet-moon-systems`, data => this.planetMoonSystemsCache = data);
  }
}
