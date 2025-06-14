import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { RegisterRequest, RegisterResponse, UserProfile } from '../models/user.interface';
import { environment } from '../../environments/environment.prod';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly http = inject(HttpClient);

  userRegister(credentials: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${environment.apiUrl}/user/register`, credentials).pipe(
      tap((response) => console.log('UserService: User register:', response))
    );
  }

    adminRegister(credentials: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${environment.apiUrl}/user/register-admin`, credentials).pipe(
      tap((response) => console.log('UserService: User register:', response))
    );
  }

  // editUserProfile

  // getAllUsers

  getUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${environment.apiUrl}/user/profile`).pipe(
      tap((profile) => {
        console.log('UserService: User profile:', profile);
        localStorage.setItem('photoUrl', profile.photoUrl || '');
      })
    );
  }

  // updateProfilePicture

  // deleteProfilePicture

  // changePassword

  // forgotPassword

  // resetPassword

  // changeUserRole

  // deleteUser
}