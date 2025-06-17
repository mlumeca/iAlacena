import { Component, OnInit, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RecipeService } from '../../services/recipe.service';
import { Recipe } from '../../models/recipe.interface';
import { CommonModule } from '@angular/common';
import { catchError, Observable, of, tap } from 'rxjs';

@Component({
    selector: 'app-recipe-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './recipe-detail.component.html',
  styleUrl: './recipe-detail.component.css'
})

export class RecipeDetailComponent implements OnInit {
  recipe!: Recipe; // Non-null assertion, initialized in subscription
  backgroundImage: string = 'assets/placeholder-cheesecake.jpg'; // Static image for now

  constructor(
    private route: ActivatedRoute,
    private recipeService: RecipeService
  ) {}

  ngOnInit(): void {
    const recipeId = this.route.snapshot.paramMap.get('id');
    if (recipeId) {
      this.recipeService.getRecipeById(Number(recipeId)).subscribe({
        next: (resp) => {
          this.recipe = resp;
          console.log('RecipeDetailComponent: recipe loaded:', resp);
          // Update backgroundImage if dynamic images are implemented later
          // this.backgroundImage = /* dynamic image logic */;
        },
        error: (err) => {
          console.error('Error fetching recipe details:', err);
          // Optionally handle error (e.g., show a message to the user or redirect)
        },
      });
    }
  }
}


// export class RecipeDetailComponent implements OnInit {
//   private recipeId = signal<number | null>(null);
//   private isLoading = signal<boolean>(true);
//   errorMessage = signal<string | null>(null);
//   recipe!: Observable<Recipe | null>;

//   // Computed properties
//   loading = computed(() => this.isLoading());
//   hasError = computed(() => this.errorMessage() !== null);

//   constructor(
//     private route: ActivatedRoute,
//     private recipeService: RecipeService
//   ) {}

//   ngOnInit(): void {
//     this.recipeId.set(Number(this.route.snapshot.paramMap.get('id')));
//     if (this.recipeId()) {
//       this.loadRecipe();
//     } else {
//       this.errorMessage.set('Invalid recipe ID');
//       this.isLoading.set(false);
//     }
//   }

//   private loadRecipe(): void {
//     if (!this.recipeId()) return;

//     this.recipe = this.recipeService.getRecipeById(this.recipeId()!).pipe(
//       tap((recipe) => {
//         console.log('RecipeDetailComponent: recipe loaded:', recipe);
//         this.isLoading.set(false);
//       }),
//       catchError((error) => {
//         console.error('Error loading recipe:', error);
//         this.errorMessage.set('Failed to load recipe details');
//         this.isLoading.set(false);
//         return of(null);
//       })
//     );
//   }
// }