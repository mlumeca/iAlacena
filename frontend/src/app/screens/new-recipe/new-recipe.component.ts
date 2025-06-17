import { Component, OnInit, signal, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RecipeService } from '../../services/recipe.service';
import { CreateRecipeRequest } from '../../models/recipe.interface';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, of, tap } from 'rxjs';

@Component({
  selector: 'app-new-recipe',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './new-recipe.component.html',
  styleUrl: './new-recipe.component.css'
})
export class NewRecipeComponent implements OnInit {
  private fb = inject(FormBuilder);
  private recipeService = inject(RecipeService);
  private router = inject(Router);
  isLoading = signal<boolean>(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  // Form group using FormBuilder
  recipeForm: FormGroup = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    portions: ['', [Validators.required, Validators.min(1), Validators.max(100)]],
    description: ['', [Validators.required, Validators.maxLength(1000)]],
    ingredients: ['', [Validators.required, Validators.maxLength(500)]],
    // image: [null] // not implemented in the service yet
  });

  ngOnInit(): void {
    // Initialize form state
  }

  onSubmit(): void {
    if (this.recipeForm.invalid) {
      this.errorMessage.set('Please fill all required fields correctly.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const recipeData: CreateRecipeRequest = {
      name: this.recipeForm.value.name,
      description: this.recipeForm.value.description,
      portions: this.recipeForm.value.portions,
      categoryIds: [] // Placeholder, to be expanded if categories are added
    };

    this.recipeService.createRecipe(recipeData).pipe(
      tap((response) => {
        console.log('Recipe created:', response);
        this.successMessage.set('Recipe created successfully!');
        this.isLoading.set(false);
        // Navigate back to recipe list or detail page after a delay
        setTimeout(() => this.router.navigate(['/recipes']), 1500);
      }),
      catchError((error) => {
        console.error('Error creating recipe:', error);
        this.errorMessage.set('Failed to create recipe. Please try again.');
        this.isLoading.set(false);
        return of(null);
      })
    ).subscribe();
  }
}