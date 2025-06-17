import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { EditRequest, InventoryListResponse, InventoryResponse, PostRequest } from '../models/inventory.interface';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class InventoryService {
  private readonly http = inject(HttpClient);

  getInventory(userId: string, params: { page?: number; size?: number }): Observable<InventoryListResponse> {
    let httpParams = new HttpParams();
    if (params.page !== undefined) httpParams = httpParams.set('page', params.page.toString());
    if (params.size !== undefined) httpParams = httpParams.set('size', params.size.toString());

    return this.http.get<InventoryListResponse>(`${environment.apiUrl}/user/${userId}/inventory`, { params: httpParams }
    );
  }

  addInventoryIngredient(userId: string, credentials: PostRequest): Observable<InventoryResponse> {
    return this.http.post<InventoryResponse>(`${environment.apiUrl}/user/${userId}/inventory`, credentials
    );
  }

  editInventoryIngredient(userId: string, id: number, credentials: EditRequest): Observable<InventoryResponse> {
    return this.http.put<InventoryResponse>(
      `${environment.apiUrl}/user/${userId}/inventory/${id}`,
      credentials,
    );
  }

  deleteInventoryIngredient(userId: string, id: number): Observable<void> {
    return this.http.delete<void>(`${environment.apiUrl}/user/${userId}/inventory/${id}`);
  }
}
