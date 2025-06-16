import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Ingredient, IngredientList, IngredientResponse, PostRequest, PutRequest } from '../models/ingredient.interface';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class IngredientService {
private readonly http = inject(HttpClient);

  getIngredientList(params: { page?: number; size?: number; name?: string; categoryId?: number }): Observable<IngredientList> {
    let httpParams = new HttpParams();
    if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());
    if (params.name) httpParams = httpParams.set('name', params.name);
    if (params.categoryId !== undefined) httpParams = httpParams.set('categoryId', params.categoryId.toString());

    return this.http.get<IngredientList>(`${environment.apiUrl}/ingredient`, { params: httpParams }
    );
  }

  getIngredientById(id: number): Observable<Ingredient> {
    return this.http.get<Ingredient>(`${environment.apiUrl}/ingredient/${id}`
    );
  }

  addIngredient(credentials: PostRequest): Observable<IngredientResponse> {
    return this.http.post<IngredientResponse>(`${environment.apiUrl}/ingredient/create`, credentials
    );
  }

  editIngredient(id:number, credentials: PutRequest): Observable<IngredientResponse> {
    return this.http.put<IngredientResponse>(
      `${environment.apiUrl}/ingredient/${id}`,
      credentials,
    );
  }

  deleteIngredient(id:number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/ingredient/${id}`);
  }
}
