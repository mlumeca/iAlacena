import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FavoritesService } from '../../services/favorites.service';
import { UserService } from '../../services/user.service';
import { Favorites, FavoriteItem, NewFavoriteRequest } from '../../models/favorites.interface';
import { Recipe } from '../../models/recipe.interface';
import { catchError, of, tap } from 'rxjs';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './favorites.component.html',
  styleUrl: './favorites.component.css'
})

export class FavoritesComponent implements OnInit {
  private favoritesService = inject(FavoritesService);
  private userService = inject(UserService);
  private router = inject(Router);

  // Signals for state management
  recipes = signal<Recipe[]>([]);
  isLoading = signal<boolean>(true);
  errorMessage = signal<string | null>(null);
  totalPages = signal<number>(0);
  currentPage = signal<number>(0);
  userId = signal<string | null>(null);

  // Pagination utilities
  private pageSize = 10;

  ngOnInit(): void {
    this.userService.getUserProfile().pipe(
      tap(profile => {
        this.userId.set(profile.id);
        this.loadFavorites();
      }),
      catchError(err => {
        this.errorMessage.set('Error fetching user profile');
        this.isLoading.set(false);
        return of(null);
      })
    ).subscribe();
  }

  loadFavorites(): void {
    if (!this.userId()) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.favoritesService.getFavorites(this.userId()!, { page: this.currentPage(), size: this.pageSize }).pipe(
      tap((response: Favorites) => {
        this.recipes.set(response.content.map(fav => ({ id: fav.recipeId } as Recipe))); // Placeholder, expand with full Recipe data if available
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      }),
      catchError(err => {
        this.errorMessage.set('Error loading favorites');
        this.isLoading.set(false);
        return of(null);
      })
    ).subscribe();
  }

  // Pagination methods
  previousPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.set(this.currentPage() - 1);
      this.loadFavorites();
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.set(this.currentPage() + 1);
      this.loadFavorites();
    }
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadFavorites();
    }
  }

  pageRange(): number[] {
    return Array.from({ length: this.totalPages() }, (_, i) => i);
  }

  paginationInfo(): { start: number; end: number; total: number } {
    const start = this.currentPage() * this.pageSize + 1;
    const end = Math.min((this.currentPage() + 1) * this.pageSize, this.totalPages() * this.pageSize);
    return { start, end, total: this.totalPages() * this.pageSize };
  }

  // Toggle favorite status
  toggleFavorite(recipeId: number): void {
    if (!this.userId()) return;

    const isFavorite = this.recipes().some(r => r.id === recipeId && this.isRecipeFavorite(recipeId));
    const request: NewFavoriteRequest = { recipeId };

    if (isFavorite) {
      const favorite = this.recipes().find(r => r.id === recipeId && this.isRecipeFavorite(recipeId));
      if (favorite) {
        this.favoritesService.deleteFavorite(this.userId()!, favorite.id).pipe(
          tap(() => this.loadFavorites()), // Reload to reflect changes
          catchError(err => {
            this.errorMessage.set('Error removing favorite');
            return of(null);
          })
        ).subscribe();
      }
    } else {
      this.favoritesService.addFavorite(this.userId()!, request).pipe(
        tap(() => this.loadFavorites()), // Reload to reflect changes
        catchError(err => {
          this.errorMessage.set('Error adding favorite');
          return of(null);
        })
      ).subscribe();
    }
  }

  // Helper to check if a recipe is favorite (simplified; adjust based on full data)
  isRecipeFavorite(recipeId: number): boolean {
    return this.recipes().some(r => r.id === recipeId); // Adjust logic if favorites are tracked separately
  }

  // Track by function for @for loop
  trackByRecipeId(index: number, recipe: Recipe): number {
    return recipe.id;
  }
}