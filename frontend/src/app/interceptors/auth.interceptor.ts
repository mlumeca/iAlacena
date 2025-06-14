import { HttpErrorResponse, HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { tap, throwError } from 'rxjs';
import { LoginResponse } from '../models/auth.interface';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const isLoginRequest = req.url.includes('/auth/login');
  let token = localStorage.getItem('token') || '';

  const newRequest = isLoginRequest
    ? req.clone()
    : req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`),
      });

  return next(newRequest).pipe(
    tap({
      next: (event) => {
        if (isLoginRequest && event instanceof HttpResponse && event.status === 200) {
          console.log('Interceptor: Login response:', event.body); // Debug log
          const loginResponse = event.body as LoginResponse;
          if (loginResponse?.id && loginResponse?.username && loginResponse?.token && loginResponse?.refreshToken) {
            console.log('Interceptor: Storing login data in localStorage'); // Debug log
            localStorage.setItem('id', loginResponse.id);
            localStorage.setItem('username', loginResponse.username);
            localStorage.setItem('token', loginResponse.token);
            localStorage.setItem('refreshToken', loginResponse.refreshToken);
          } else {
            console.warn('Interceptor: Invalid login response:', loginResponse); // Debug log
          }
        }
      },
      error: (err: HttpErrorResponse) => {
        console.error('Interceptor: Error:', err);
        if (err.status === 401) {
          console.log('Interceptor: 401 detected, clearing localStorage');
          localStorage.clear();
          window.location.href = '/login';
        }
        return throwError(() => err);
      },
    })
  );
};