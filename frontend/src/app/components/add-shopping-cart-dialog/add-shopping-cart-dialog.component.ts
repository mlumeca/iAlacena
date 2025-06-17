import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { addCartIngredient, EditRequest } from '../../models/shopping-cart.interface';
import { MatSnackBar } from '@angular/material/snack-bar';
import { IngredientService } from '../../services/ingredient.service';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-add-shopping-cart-dialog',
  standalone: true,
  imports: [MatDialogModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule],
  templateUrl: './add-shopping-cart-dialog.component.html',
  styleUrl: './add-shopping-cart-dialog.component.css'
})

export class AddShoppingCartDialogComponent {
  form: FormGroup;
  isEdit: boolean = false;

  constructor(
    public dialogRef: MatDialogRef<AddShoppingCartDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { ingredientId?: number; quantity?: number; isEdit?: boolean } | null,
    private fb: FormBuilder,
    private snackBar: MatSnackBar,
    private ingredientService: IngredientService // Optional
  ) {
    // Safely handle null data with default values
    const safeData = data || { isEdit: false, ingredientId: undefined, quantity: undefined };
    this.isEdit = !!safeData.isEdit;
    this.form = this.fb.group({
      ingredientId: [safeData.ingredientId || '', [Validators.required, Validators.min(1)]],
      quantity: [safeData.quantity || 1, [Validators.required, Validators.min(1), Validators.max(100)]]
    });

    // If editing, make ingredientId read-only
    if (this.isEdit) {
      this.form.get('ingredientId')?.disable();
    }
  }

  onSave(): void {
    if (this.form.invalid) {
      this.snackBar.open('Please fill all fields correctly.', 'Close', { duration: 3000 });
      return;
    }

    const result = this.isEdit
      ? { quantity: this.form.value.quantity } as EditRequest
      : { ingredientId: this.form.value.ingredientId } as addCartIngredient;

    this.dialogRef.close(result);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}