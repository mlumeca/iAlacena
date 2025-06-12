import { Routes } from '@angular/router';
import { LoginComponent } from './screens/login/login.component';
import { SignInComponent } from './screens/sign-in/sign-in.component';
import { HomeComponent } from './screens/home/home.component';

export const routes: Routes = [
    {path: '', redirectTo: '/login', pathMatch: 'full'},
    {path: 'login', component: LoginComponent},
    {path: 'sign-in', component: SignInComponent},
    {path: 'home', component: HomeComponent},
    // {path: 'cars/:id', component: CarDetailsComponent,}, ...
    {path: '**', redirectTo: '/login', pathMatch: 'full'},
];