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
          const loginResponse = event.body as LoginResponse;
          if (loginResponse?.id && loginResponse?.username && loginResponse?.token && loginResponse?.refreshToken) {
            localStorage.setItem('id', loginResponse.id);
            localStorage.setItem('username', loginResponse.username);
            localStorage.setItem('token', loginResponse.token);
            localStorage.setItem('refreshToken', loginResponse.refreshToken);
          }
        }
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 401) {
          // Handle unauthorized (e.g., clear localStorage, redirect to login)
          localStorage.clear();
          window.location.href = '/login';
        }
        return throwError(() => err);
      },
    })
  );
};