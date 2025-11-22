import { Injectable } from '@angular/core';
import {LookupService} from "./lookup.service";
import {map, Observable} from "rxjs";

export interface filterOptions {
  id: number,
  option: string,
}

@Injectable({
  providedIn: 'root'
})
export class FilterService {

  constructor(private lookup: LookupService) {}

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
