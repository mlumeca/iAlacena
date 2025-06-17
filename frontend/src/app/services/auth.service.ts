import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment.prod';
import { LoginRequest, LoginResponse, LogoutRequest } from '../models/auth.interface';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${environment.apiUrl}/auth/login`, credentials
    );
  }

  logout(credentials: LogoutRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/logout`, credentials).pipe(
      tap({
        next: () => {
          console.log('AuthService: Logout API call successful'); // Debug log
          localStorage.clear();
          this.router.navigateByUrl('/login').then((success) => {
            console.log('Navigation to /login after logout:', success ? 'Success' : 'Failed'); // Debug log
          });
        },
        error: (err) => {
          console.error('AuthService: Logout API error:', err); // Debug log
          localStorage.clear();
          this.router.navigateByUrl('/login');
        },
      })
    );
  }

  isAuthenticated(): boolean {
    const token = localStorage.getItem('token');
    // console.log('AuthService: isAuthenticated, token exists:', !!token);
    return !!token;
  }
}
