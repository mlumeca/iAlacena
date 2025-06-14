import { Routes } from '@angular/router';
import { LoginComponent } from './screens/login/login.component';
import { SignInComponent } from './screens/sign-in/sign-in.component';
import { HomeComponent } from './screens/home/home.component';
import { authGuard, noAuthGuard } from './guards/auth.guard';
import { ProfileComponent } from './screens/profile/profile.component';
import { FavoritesComponent } from './screens/favorites/favorites.component';
import { ShoppingCartComponent } from './screens/shopping-cart/shopping-cart.component';
import { InventoryComponent } from './screens/inventory/inventory.component';
import { NewRecipeComponent } from './screens/new-recipe/new-recipe.component';
import { RecipeDetailComponent } from './screens/recipe-detail/recipe-detail.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  
  { path: 'login', component: LoginComponent, canActivate: [noAuthGuard] },
  { path: 'sign-in', component: SignInComponent, canActivate: [noAuthGuard] },
  
  { path: 'home', component: HomeComponent, canActivate: [authGuard] },
  { path: 'recipe-detail', component: RecipeDetailComponent, canActivate: [authGuard] },
  { path: 'new-recipe', component: NewRecipeComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'favorites', component: FavoritesComponent, canActivate: [authGuard] },
  { path: 'shopping-cart', component: ShoppingCartComponent, canActivate: [authGuard] },
  { path: 'inventory', component: InventoryComponent, canActivate: [authGuard] },
  
  { path: '**', redirectTo: '/login', pathMatch: 'full' },
];