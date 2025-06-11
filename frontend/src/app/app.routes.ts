import { Routes } from '@angular/router';

export const routes: Routes = [
    {path: '', redirectTo: '/login', pathMatch: 'full'},
    // {path: 'login', component: LoginComponent},
    // {path: 'cars/:id', component: CarDetailsComponent,}, ...
    {path: '**', redirectTo: '/login', pathMatch: 'full'},
];