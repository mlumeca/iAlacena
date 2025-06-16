import { Component, OnInit } from '@angular/core';
import { catchError, Observable, of, tap } from 'rxjs';
import { Recipe } from '../../models/recipe.interface';
import { RecipeService } from '../../services/recipe.service';
import { ActivatedRoute } from '@angular/router';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-recipe-detail',
  standalone: true,
  imports: [AsyncPipe],
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.css'
})
export class RecipeDetailComponent implements OnInit {
  recipe$!: Observable<Recipe>;
  recipeId!: number;
  isLoading: boolean = true;
  error: string | null = null;

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
    this.recipe$ = this.recipeService.getRecipeById(this.recipeId).pipe(
      tap((recipe) => {
        console.log('RecipeDetailComponent: recipe loaded:', recipe);
        this.isLoading = false;
      }),
      catchError((error) => {
        console.error('Error loading recipe:', error);
        this.error = 'Failed to load recipe details';
        this.isLoading = false;
        return of(null); // Return null to avoid breaking the stream
      })
    );
  }
}

