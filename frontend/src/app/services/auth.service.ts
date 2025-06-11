import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.prod';
import { LoginRequest, LoginResponse, LogoutRequest } from '../models/auth.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) {}

  login(credentials:LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${environment.apiUrl}/auth/login`,
      credentials,
    );
  }

  logout(credentials:LogoutRequest): Observable<void> {
    return this.http.post<void>(
      `${environment.apiUrl}/auth/logout`,
      credentials,
    );
  }
}
