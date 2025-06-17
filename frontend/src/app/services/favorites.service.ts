import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Favorites, NewFavoriteRequest, NewFavoriteResponse } from '../models/favorites.interface';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {
  private readonly http = inject(HttpClient);

  getFavorites(userId: string, params: { page?: number; size?: number }): Observable<Favorites> {
    let httpParams = new HttpParams();
    if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());

    return this.http.get<Favorites>(`${environment.apiUrl}/user/${userId}/favorites`, { params: httpParams }
    );
  }

  addFavorite(userId: string, fav: NewFavoriteRequest): Observable<NewFavoriteResponse> {
    return this.http.post<NewFavoriteResponse>(`${environment.apiUrl}/user/${userId}/favorites`, fav
    );
  }

  deleteFavorite(userId: string, id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/user/${userId}/favorites/${id}`);
  }
}
