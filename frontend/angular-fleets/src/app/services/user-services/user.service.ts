import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {User} from "../../models/user/user";
import {Observable} from "rxjs";
import {environment} from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl = `${environment.apiBaseUrl}/users`;

  constructor(private httpClient: HttpClient) { }

  getUserById(userId: number): Observable<User> {
    const url = `${this.baseUrl}/${userId}`;
    return this.httpClient.get<User>(url);
  }
}
