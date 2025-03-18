import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})

// Service for accessing lookup table data from database so that it can be used in forms
export class LookupService {

  private baseUrl = `${environment.apiBaseUrl}`;

  constructor(private http: HttpClient) { }

  getServerRegions(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/server-regions`)
  }

  getGameEnvironments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/game-environments`)
  }

  getGameExperiences(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/game-experiences`)
  }

  getPlayStyles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/play-styles`)
  }

  getLegalities(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/legalities`);
  }

  getGroupStatuses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/group-statuses`)
  }

  getGameplayCategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/gameplay-categories`);
  }

  getGameplaySubcategories(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/gameplay-subcategories`);
  }

  getPvpStatuses(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/pvp-statuses`);
  }

  //Broader systems: Stanton, Pyro, etc.
  getPlanetarySystems(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/planetary-systems`);
  }

  //Subsystems: Hurston, Microtech, etc.
  getPlanetMoonSystems(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/lookup/planet-moon-systems`);
  }
}
