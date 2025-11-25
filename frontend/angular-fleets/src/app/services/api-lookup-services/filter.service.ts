import {Injectable} from '@angular/core';
import {LookupService} from "./lookup.service";
import {BehaviorSubject, map, Observable, of, Subject, takeUntil} from "rxjs";

export interface filterOptions {
  id: number,
  option: string,
}

export interface ListingFilterState {
  searchInput: string | null;

  server: filterOptions | null;
  environment: filterOptions | null;
  experience: filterOptions | null;
  playStyle: filterOptions | null;
  category: filterOptions | null;
  subcategory: filterOptions | null;
  legality: filterOptions | null;
  pvpStatus: filterOptions | null;
  system: filterOptions | null;
  planetMoonSystem: filterOptions | null;
  groupStatus: filterOptions | null;
  dateStart: string | null;
  dateEnd: string | null;
  commsOption: filterOptions | null;
}

export type FilterOptionKey =
  | 'server'
  | 'environment'
  | 'experience'
  | 'playStyle'
  | 'category'
  | 'subcategory'
  | 'legality'
  | 'pvpStatus'
  | 'system'
  | 'planetMoonSystem'
  | 'groupStatus'
  | 'commsOption'
  | 'dateStart'
  | 'dateEnd'


@Injectable({
  providedIn: 'root'
})
export class FilterService {
  private destroy$ = new Subject<void>();
  constructor(private lookup: LookupService) {}

  private state: ListingFilterState = {
    searchInput: null,
    server: null,
    environment: null,
    experience: null,
    playStyle: null,
    category: null,
    subcategory: null,
    legality: null,
    pvpStatus: null,
    system: null,
    planetMoonSystem: null,
    groupStatus: null,
    dateStart: null,
    dateEnd: null,
    commsOption: null,
  }

  private stateSubject = new BehaviorSubject<ListingFilterState>(this.state);
  readonly state$ = this.stateSubject.asObservable();

  update<K extends keyof ListingFilterState>(key: K, value: ListingFilterState[K]) {
    this.state = { ...this.state, [key]: value };
    this.stateSubject.next(this.state)
  }

  clearFilters() {
    this.state = {
      searchInput: null,
      server: null,
      environment: null,
      experience: null,
      playStyle: null,
      category: null,
      subcategory: null,
      legality: null,
      pvpStatus: null,
      system: null,
      planetMoonSystem: null,
      groupStatus: null,
      dateStart: null,
      dateEnd: null,
      commsOption: null,
    }
  }

  updateOption(key: FilterOptionKey, value: filterOptions | null) {
    this.update(key, value);
  }

  pullState(): ListingFilterState {
    return this.state;
  }

  filterGroupStatus(): Observable<filterOptions[]> {
    return this.lookup.getGroupStatuses().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.groupStatusId,
          option: data.groupStatus,
        }))
      )
    );
  }

  filterServerRegions(): Observable<filterOptions[]> {
    return this.lookup.getServerRegions().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.serverId,
          option: data.servername,
        }))
      )
    );
  }

  filterEnvironments(): Observable<filterOptions[]> {
    return this.lookup.getGameEnvironments().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.environmentId,
          option: data.environmentType,
        }))
      )
    );
  }

  filterExperiences(): Observable<filterOptions[]> {
    return this.lookup.getGameExperiences().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.experienceId,
          option: data.experienceType,
        }))
      )
    );
  }

  filterCategories(): Observable<filterOptions[]> {
    return this.lookup.getGameplayCategories().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.gameplayCategoryId,
          option: data.gameplayCategoryName,
        }))
      )
    );
  }

  filterSubcategories(parent: number): Observable<filterOptions[]> {
    if(!parent) {
      return of([]);
    }

    return this.lookup.getGameplaySubcategories().pipe(
      map(arr =>
        arr
          .filter(subcat => subcat.gameplayCategoryId === parent)
          .map(data => ({
          id: data.subcategoryId,
          option: data.subcategoryName,
        }))
      )
    );
  }

  filterSystems(): Observable<filterOptions[]> {
    return this.lookup.getPlanetarySystems().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.systemId,
          option: data.systemName,
        }))
      )
    );
  }

  filterPlanets(parent: number): Observable<filterOptions[]> {
    if(!parent) {
      return of([]);
    }

    return this.lookup.getPlanetMoonSystems().pipe(
      map(arr =>
        arr
          .filter(planet => planet.systemId === parent)
          .map(data => ({
            id: data.planetId,
            option: data.planetName,
        })))
    )
  }

  filterPvp(): Observable<filterOptions[]> {
    return this.lookup.getPvpStatuses().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.pvpStatusId,
          option: data.pvpStatus,
        }))
      )
    );
  }

  filterLegalities(): Observable<filterOptions[]> {
    return this.lookup.getLegalities().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.legalityId,
          option: data.legalityStatus,
        }))
      )
    );
  }

  filterPlayStyles(): Observable<filterOptions[]> {
    return this.lookup.getPlayStyles().pipe(
      map(arr =>
        arr.map(data => ({
          id: data.styleId,
          option: data.playStyle,
        }))
      )
    );
  }
}
