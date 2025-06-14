import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const isAuthenticated = !!localStorage.getItem('token');
  console.log('AuthGuard: isAuthenticated=', isAuthenticated, 'currentUrl=', state.url);

  if (isAuthenticated) {
    return true;
  }
  console.log('AuthGuard: Redirecting to /login from', state.url);
  return router.createUrlTree(['/login']);
};

export const noAuthGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const isAuthenticated = !!localStorage.getItem('token');
  console.log('NoAuthGuard: isAuthenticated=', isAuthenticated, 'currentUrl=', state.url);

  if (!isAuthenticated) {
    return true;
  }
  console.log('NoAuthGuard: Redirecting to /home from', state.url);
  return router.createUrlTree(['/home']);
};
