import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RecipeService } from '../../services/recipe.service';
import { Recipe } from '../../models/recipe.interface';
import { catchError, of, tap } from 'rxjs';

@Component({
    selector: 'app-recipe-detail',
  standalone: true,
  imports: [],
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.css'
})
export class RecipeDetailComponent implements OnInit {
    recipe = signal<Recipe>();
  recipeId!: number;
  isLoading = signal<boolean>(false);
  error: string | null = null;
  errorMessage = signal<string | null>(null);

  constructor(
    private route: ActivatedRoute,
    private recipeService: RecipeService
  ) {}

  ngOnInit(): void {
    // Get the recipe ID from the route parameter
    this.recipeId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.recipeId) {
      this.loadRecipe();
    } else {
      this.error = 'Invalid recipe ID';
      this.isLoading = false;
    }
  }

  private loadRecipe(): void {


    this.recipeService.getRecipeById(this.recipeId).pipe(
      tap((recipe) => {
        console.log('RecipeDetailComponent: recipe loaded:', recipe);
        this.isLoading = false;
      }),
      catchError((error) => {
        console.error('Error loading recipe:', error);
        this.error = 'Failed to load recipe details';
        this.isLoading = false;
        return of(null);
      })
    );
  }
}