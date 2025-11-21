import { Injectable } from '@angular/core';
import {LookupService} from "./lookup.service";
import {map, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  constructor(private lookup: LookupService) {}

  filterGroupStatus(): Observable<string[]> {
    return this.lookup.getGroupStatuses().pipe(
      map(arr =>
        arr.map(d => d.groupStatus)
      )
    );
  }

  filterServerRegions(): Observable<string[]> {
    return this.lookup.getServerRegions().pipe(
      map(arr => arr.map(d => d.servername))
    );
  }

  filterEnvironments(): Observable<string[]> {
    return this.lookup.getGameEnvironments().pipe(
      map(arr => arr.map(d => d.environmentType))
    )
  }

  filterExperiences(): Observable<string[]> {
    return this.lookup.getGameExperiences().pipe(
      map(arr => arr.map(d => d.experienceType))
    )
  }

  filterCategories(): Observable<string[]> {
    return this.lookup.getGameplayCategories().pipe(
      map(arr => arr.map(d => d.gameplayCategoryName))
    );
  }

  filterSystems(): Observable<string[]> {
    return this.lookup.getPlanetarySystems().pipe(
      map(arr => arr.map(d => d.systemName))
    );
  }

  filterPvp(): Observable<string[]> {
    return this.lookup.getPvpStatuses().pipe(
      map(arr => arr.map(d => d.pvpStatus))
    );
  }

  filterLegalities(): Observable<string[]> {
    return this.lookup.getLegalities().pipe(
      map(arr => arr.map(d => d.legalityStatus))
    );
  }

  filterPlayStyles(): Observable<string[]> {
    return this.lookup.getPlayStyles().pipe(
      map(arr => arr.map(d => d.playStyle))
    );
  }
}
