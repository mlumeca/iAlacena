import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.prod';
import { addCartIngredient, EditRequest, EditResponse, ShoppingCart, ShoppingCartItem } from '../models/shopping-cart.interface';

@Injectable({
  providedIn: 'root'
})
export class ShoppingCartService {
  private readonly http = inject(HttpClient);

  getShoppingCart(userId: string): Observable<ShoppingCart> {
    return this.http.get<ShoppingCart>(`${environment.apiUrl}/user/${userId}/shopping-cart`
    );
  }

  addShoppingCartIngredient(userId: string, ingredient: addCartIngredient): Observable<ShoppingCartItem> {
    return this.http.post<ShoppingCartItem>(`${environment.apiUrl}/user/${userId}/shopping-cart`, ingredient
    );
  }

  // put `${environment.apiUrl}/user/${userId}/shopping-cart/${id}`
  editShoppingCartIngredient(userId: string, id: number, quantity: EditRequest): Observable<EditResponse> {
    return this.http.put<EditResponse>(`${environment.apiUrl}/user/${userId}/shopping-cart/${id}`, quantity
    );
  }

  deleteShoppingCartIngredient(userId: string, id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/user/${userId}/shopping-cart/${id}`);
  }

  deleteShoppingCart(userId: string): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/user/${userId}/shopping-cart`);
  }

}
