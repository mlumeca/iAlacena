import { inject, Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { CreateRecipeRequest, Recipe, RecipeListResponse } from '../models/recipe.interface';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  private readonly http = inject(HttpClient);
  
  getRecipeList(params: { page?: number; size?: number; name?: string; categoryId?: number }): Observable<RecipeListResponse> {
    let httpParams = new HttpParams();
    if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.categoryId !== undefined) httpParams = httpParams.set('categoryId', params.categoryId.toString());

    return this.http.get<RecipeListResponse>(`${environment.apiUrl}/recipe`, { params: httpParams }).pipe(
      tap((response) => console.log('RecipeService: getRecipeList response:', response))
    );
  }

  getRecipeById(id: number): Observable<Recipe> {
    return this.http.get<Recipe>(`${environment.apiUrl}/recipe/${id}`).pipe(
      tap((response) => console.log('UserService: User register:', response))
    );
  }

  createRecipe(recipe: CreateRecipeRequest): Observable<Recipe> {
    return this.http.post<Recipe>(`${environment.apiUrl}/recipe/create`, recipe).pipe(
      tap((response) => console.log('UserService: User register:', response))
    );
  }

  // editRecipe(recipe: RecipeBody)

  // editRecipeIngredients()

  // deleteRecipe()

}
