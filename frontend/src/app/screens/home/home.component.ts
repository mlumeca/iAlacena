import { Component, computed, inject, signal } from '@angular/core';
import { Recipe, RecipeListResponse } from '../../models/recipe.interface';
import { RecipeService } from '../../services/recipe.service';
import { debounceTime, Subject } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CategoriesPipe } from '../../pipes/categories.pipe';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, CategoriesPipe],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  private readonly recipeService = inject(RecipeService);

  // Signals for state management
  recipes = signal<Recipe[]>([]);
  totalPages = signal<number>(1);
  currentPage = signal<number>(0); // 0-based, as in the backend
  pageSize = signal<number>(10);
  totalElements = signal<number>(0);
  searchTerm = signal<string>('');
  categoryId = signal<number | null>(null);
  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);

  // Debounced search
  private searchSubject = new Subject<string>();

  constructor() {
    // Debounce search input delays the emission of values from an 
    // observable until the time has passed. 
    // reduces the number of API calls the user types
    this.searchSubject.pipe(debounceTime(300)).subscribe((term) => {
      this.searchTerm.set(term);
      this.currentPage.set(0); // Reset to first page on search
      this.loadRecipes();
    });

    // Initial load
    this.loadRecipes();
  }

  // Computed signal for pagination info, supports reactive state management
  // Automatically updates when currentPage, pageSize, or totalElements changes
  paginationInfo = computed(() => ({
    start: this.currentPage() * this.pageSize() + 1,
    end: Math.min((this.currentPage() + 1) * this.pageSize(), this.totalElements()),
    total: this.totalElements(),
  }));

    pageRange = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    const delta = 2;
    const range: number[] = [];
    for (let i = Math.max(0, current - delta); i <= Math.min(total - 1, current + delta); i++) {
      range.push(i);
    }
    return range;
  });

  // Load recipes from service
  private loadRecipes(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    const params = {
      page: this.currentPage(),
      size: this.pageSize(),
      name: this.searchTerm() || undefined,
      categoryId: this.categoryId() || undefined,
    };

    this.recipeService.getRecipeList(params).subscribe({
      next: (response: RecipeListResponse) => {
        this.recipes.set(response.items);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
        this.isLoading.set(false);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        let message = 'Error al cargar las recetas.';
        if (err.status === 0) {
          message = 'No se pudo conectar al servidor.';
        } else if (err.status === 404) {
          message = 'No se encontraron recetas.';
        } else if (err.error?.message) {
          message = err.error.message;
        }
        this.errorMessage.set(message);
        console.error('Recipe load error:', err);
      },
    });
  }

  // Handle search input
  onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  // Pagination controls
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadRecipes();
    }
  }

  nextPage(): void {
    this.goToPage(this.currentPage() + 1);
  }

  previousPage(): void {
    this.goToPage(this.currentPage() - 1);
  }

  // TrackBy for performance
  trackByRecipeId(index: number, recipe: Recipe): number {
    return recipe.id;
  }
}

