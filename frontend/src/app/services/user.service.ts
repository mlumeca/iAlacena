import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { EditPasswordRequest, EditProfileRequest, EditProfileResponse, EditRoleRequest, ForgotPasswordRequest, PostAvatarRequest, PostAvatarResponse, ProfileListResponse, RegisterRequest, RegisterResponse, ResetPasswordRequest, UserProfile } from '../models/user.interface';
import { environment } from '../../environments/environment.prod';
import { HttpClient, HttpParams } from '@angular/common/http';

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
      tap((response) => console.log('UserService: Admin register:', response))
    );
  }

  editUserProfile(userId:string, credentials: EditProfileRequest): Observable<EditProfileResponse> {
    return this.http.put<EditProfileResponse>(
      `${environment.apiUrl}/profile/${userId}`,
      credentials,
    );
  }

  // Only ADMIN
  editUserRole(id: string, credentials: EditRoleRequest): Observable<EditProfileResponse> {
    return this.http.put<EditProfileResponse>(
      `${environment.apiUrl}/user/${id}/role`,
      credentials,
    );
  }

  // Only ADMIN
  getUserList(params: { page?: number; size?: number }): Observable<ProfileListResponse> {
    let httpParams = new HttpParams();
    if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());

    return this.http.get<ProfileListResponse>(`${environment.apiUrl}/user/all`, { params: httpParams }
    );
  }
  getUserProfile(userId:string): Observable<UserProfile> {
    const url = `${environment.apiUrl}/profile/${userId}`;
    console.log('UserService: Fetching profile from', url);
    return this.http.get<UserProfile>(url).pipe(
      tap((profile) => {
        console.log('UserService: User profile:', profile);
        localStorage.setItem('photoUrl', profile.photoUrl || '');
      })
    );
  }
  updateProfilePicture(id: string, credentials: PostAvatarRequest): Observable<PostAvatarResponse> {
    return this.http.put<PostAvatarResponse>(
      `${environment.apiUrl}/user/${id}/profile-picture`,
      credentials,
    );
  }

  deleteProfilePicture(id: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/user/${id}/profile-picture`);
  }

  changePassword(credentials: EditPasswordRequest): Observable<EditProfileResponse> {
    return this.http.put<EditProfileResponse>(
      `${environment.apiUrl}/user/change-password`,
      credentials,
    );
  }


  forgotPassword(credentials: ForgotPasswordRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/user/forgot-password`, credentials
    );
  }

  resetPassword(credentials: ResetPasswordRequest): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/user/reset-password`, credentials
    );
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/user/${id}`);
  }
}