import { Routes } from '@angular/router';
import { LoginComponent } from './screens/login/login.component';
import { SignInComponent } from './screens/sign-in/sign-in.component';
import { HomeComponent } from './screens/home/home.component';
import { authGuard, noAuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  
  { path: 'login', component: LoginComponent, canActivate: [noAuthGuard] },
  { path: 'sign-in', component: SignInComponent, canActivate: [noAuthGuard] },
  
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'recipe-detail', component: HomeComponent, canActivate: [authGuard] },
  { path: 'new-recipe', component: HomeComponent, canActivate: [authGuard] },
  { path: 'profile', component: HomeComponent, canActivate: [authGuard] },
  { path: 'favorites', component: HomeComponent, canActivate: [authGuard] },
  { path: 'shopping-cart', component: HomeComponent, canActivate: [authGuard] },
  { path: 'inventory', component: HomeComponent, canActivate: [authGuard] },
  
  { path: '**', redirectTo: '/login', pathMatch: 'full' },
];