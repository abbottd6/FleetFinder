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
    return this.http.get<any[]>('/api/gameEnvironments')
  }

  getGameExperiences(): Observable<any[]> {
    return this.http.get<any[]>('/api/gameExperiences')
  }

  getPlayStyles(): Observable<any[]> {
    return this.http.get<any[]>('/api/playStyles')
  }

  getLegalities(): Observable<any[]> {
    return this.http.get<any[]>('/api/legalities');
  }

  getGroupStatuses(): Observable<any[]> {
    return this.http.get<any[]>('/api/groupStatuses')
  }

  getGameplayCategories(): Observable<any[]> {
    return this.http.get<any[]>('/api/gameplayCategories');
  }

  getGameplaySubcategories(): Observable<any[]> {
    return this.http.get<any[]>('/api/gameplaySubcategories');
  }

  getPvpStatuses(): Observable<any[]> {
    return this.http.get<any[]>('/api/pvpStatuses');
  }

  //Large scale systems: Stanton, Pyro, etc.
  getPlanetarySystems(): Observable<any[]> {
    return this.http.get<any[]>('/api/planetarySystems');
  }

  //Small scale systems: Hurston, Microtech, etc.
  getPlanetMoonSystems(): Observable<any[]> {
    return this.http.get<any[]>('/api/planetMoonSystems');
  }
}
