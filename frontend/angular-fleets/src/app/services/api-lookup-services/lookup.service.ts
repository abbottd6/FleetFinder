import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})

// Service for accessing lookup table data from database so that it can be used in forms
export class LookupService {

  constructor(private http: HttpClient) { }

  getServerRegions(): Observable<any[]> {
    return this.http.get<any[]>('/api/serverRegions')
  }

  getGameEnvironments(): Observable<any[]> {
    return this.http.get<any[]>('/api/lookup/game-environments')
  }

  getGameExperiences(): Observable<any[]> {
    return this.http.get<any[]>('/api/lookup/game-experiences')
  }

  getPlayStyles(): Observable<any[]> {
    return this.http.get<any[]>('/api/playStyles')
  }

  getLegalities(): Observable<any[]> {
    return this.http.get<any[]>('/api/lookup/legalities');
  }

  getGroupStatuses(): Observable<any[]> {
    return this.http.get<any[]>('/api/lookup/group-statuses')
  }

  getGameplayCategories(): Observable<any[]> {
    return this.http.get<any[]>('/api/lookup/gameplay-categories');
  }

  getGameplaySubcategories(): Observable<any[]> {
    return this.http.get<any[]>('/api/lookup/gameplay-subcategories');
  }

  getPvpStatuses(): Observable<any[]> {
    return this.http.get<any[]>('/api/pvpStatuses');
  }

  //Broader systems: Stanton, Pyro, etc.
  getPlanetarySystems(): Observable<any[]> {
    return this.http.get<any[]>('/api/lookup/planetary-systems');
  }

  //Subsystems: Hurston, Microtech, etc.
  getPlanetMoonSystems(): Observable<any[]> {
    return this.http.get<any[]>('/api/lookup/planet-moon-systems');
  }
}
