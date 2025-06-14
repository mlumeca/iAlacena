import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { UserProfile } from '../models/user.interface';
import { environment } from '../../environments/environment.prod';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly http = inject(HttpClient);

  getUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${environment.apiUrl}/auth/profile`).pipe(
      tap((profile) => {
        console.log('AuthService: User profile:', profile);
        localStorage.setItem('photoUrl', profile.photoUrl || '');
      })
    );
  }
}