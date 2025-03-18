import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import { environment } from '../../../environment/environment';

@Injectable({
  providedIn: 'root'
})

// Service for accessing lookup table data from database so that it can be used in forms
export class LookupService {

  private baseUrl = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient) { }

  getServerRegions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/server-regions`)
  }

  getGameEnvironments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/game-environments`)
  }

  getGameExperiences(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/game-experiences`)
  }

  getPlayStyles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/play-styles`)
  }

  getLegalities(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/legalities`);
  }

  getGroupStatuses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/group-statuses`)
  }

  getGameplayCategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/gameplay-categories`);
  }

  getGameplaySubcategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/gameplay-subcategories`);
  }

  getPvpStatuses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/pvp-statuses`);
  }

  //Broader systems: Stanton, Pyro, etc.
  getPlanetarySystems(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/planetary-systems`);
  }

  //Subsystems: Hurston, Microtech, etc.
  getPlanetMoonSystems(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/api/lookup/planet-moon-systems`);
  }
}
