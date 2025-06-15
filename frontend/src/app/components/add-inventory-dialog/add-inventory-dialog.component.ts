import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogContent, MatDialogActions } from '@angular/material/dialog';
import { IngredientService } from '../../services/ingredient.service';
import { Ingredient } from '../../models/ingredient.interface';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged, map, switchMap } from 'rxjs/operators';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

@Component({
  selector: 'app-add-inventory-dialog',
  standalone: true,
  imports: [MatDialogContent, MatDialogActions, MatFormFieldModule, MatFormFieldModule, MatAutocompleteModule],
  templateUrl: './add-inventory-dialog.component.html',
  styleUrl: './add-inventory-dialog.component.css'
})
export class AddInventoryDialogComponent {
  form: FormGroup;
  filteredIngredients: Ingredient[] = [];

  constructor(
    private fb: FormBuilder,
    private ingredientService: IngredientService,
    public dialogRef: MatDialogRef<AddInventoryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data?: { ingredientId: number; ingredientName?: string; quantity: number; isEdit: boolean }
  ) {
    this.form = this.fb.group({
      ingredient: [null, data?.isEdit ? [] : [Validators.required]],
      quantity: [data?.quantity || 0, [Validators.required, Validators.min(0)]]
    });

    if (!data?.isEdit) {
      this.form.get('ingredient')?.valueChanges.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        switchMap(value => this.searchIngredients(value))
      ).subscribe(ingredients => {
        this.filteredIngredients = ingredients;
      });
    }
  }

  searchIngredients(value: string | Ingredient): Observable<Ingredient[]> {
    const name = typeof value === 'string' ? value : value?.name || '';
    return this.ingredientService.getIngredientList({ name, page: 0, size: 10 }).pipe(
      map(response => response.items)
    );
  }

  displayIngredient(ingredient?: Ingredient): string {
    return ingredient ? ingredient.name : '';
  }

  onSave() {
    if (this.form.valid) {
      const value = this.form.value;
      const result = {
        ingredientId: this.data?.isEdit ? this.data.ingredientId : value.ingredient.id,
        quantity: value.quantity
      };
      this.dialogRef.close(result);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}