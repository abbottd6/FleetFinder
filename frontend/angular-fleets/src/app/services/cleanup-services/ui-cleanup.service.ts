import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UiCleanupService {

  private uiCleanupUrl: string = `${environment.apiBaseUrl}/be-busy`;

  constructor(private httpClient: HttpClient) {}

  cleanClickedListings(clickedIds: number[]) {
    return this.httpClient.put<any>(`${this.uiCleanupUrl}/clicked-clean`, clickedIds);
  }
}
