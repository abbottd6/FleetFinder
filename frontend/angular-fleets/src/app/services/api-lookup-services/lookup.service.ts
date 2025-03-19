import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable, of, tap} from "rxjs";
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})

// Service for accessing lookup table data from database so that it can be used in forms
export class LookupService {

  private baseUrl = `${environment.apiBaseUrl}`;

  //caches
  private serverRegionsCache: any[] | null = null;
  private gameEnvironmentsCache: any[] | null = null;
  private gameExperiencesCache: any[] | null = null;
  private playStylesCache: any[] | null = null;
  private legalitiesCache: any[] | null = null;
  private groupStatusesCache: any[] | null = null;
  private gameplayCategoriesCache: any[] | null = null;
  private gameplaySubCategoriesCache: any[] | null = null;
  private pvpStatusesCache: any[] | null = null;
  private planetarySystemCache: any[] | null = null;
  private planetMoonSystemsCache: any[] | null = null;

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

  getServerRegions(): Observable<any[]> {
    return this.getCached(this.serverRegionsCache,
      `/lookup/server-regions`, data => this.serverRegionsCache = data);
  }

  getGameEnvironments(): Observable<any[]> {
    return this.getCached(this.gameEnvironmentsCache,
      `/lookup/game-environments`, data => this.gameEnvironmentsCache = data);
  }

  getGameExperiences(): Observable<any[]> {
    return this.getCached(this.gameExperiencesCache,
      `/lookup/game-experiences`, data => this.gameExperiencesCache = data);
  }

  getPlayStyles(): Observable<any[]> {
    return this.getCached(this.playStylesCache,
      `/lookup/play-styles`, data => this.playStylesCache = data);
  }

  getLegalities(): Observable<any[]> {
    return this.getCached(this.legalitiesCache,
      `/lookup/legalities`, data => this.legalitiesCache = data);
  }

  getGroupStatuses(): Observable<any[]> {
    return this.getCached(this.groupStatusesCache,
      `/lookup/group-statuses`, data => this.groupStatusesCache = data);
  }

  getGameplayCategories(): Observable<any[]> {
    return this.getCached(this.gameplayCategoriesCache,
      `/lookup/gameplay-categories`, data => this.gameplayCategoriesCache = data);
  }

  getGameplaySubcategories(): Observable<any[]> {
    return this.getCached(this.gameplaySubCategoriesCache,
      `/lookup/gameplay-subcategories`, data => this.gameplaySubCategoriesCache = data);
  }

  getPvpStatuses(): Observable<any[]> {
    return this.getCached(this.pvpStatusesCache,
      `/lookup/pvp-statuses`, data => this.pvpStatusesCache = data);
  }

  //Broader systems: Stanton, Pyro, etc.
  getPlanetarySystems(): Observable<any[]> {
    return this.getCached(this.planetarySystemCache,
      `/lookup/planetary-systems`, data => this.planetarySystemCache = data);
  }

  //Subsystems: Hurston, Microtech, etc.
  getPlanetMoonSystems(): Observable<any[]> {
    return this.getCached(this.planetMoonSystemsCache,
      `/lookup/planet-moon-systems`, data => this.planetMoonSystemsCache = data);
  }
}
