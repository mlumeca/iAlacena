import { Component, OnInit } from '@angular/core';
import { InventoryService } from '../../services/inventory.service';
import { IngredientService } from '../../services/ingredient.service';
import { UserService } from '../../services/user.service';
import { InventoryItem, InventoryListResponse } from '../../models/inventory.interface';
import { Ingredient } from '../../models/ingredient.interface';
import { MatDialog } from '@angular/material/dialog';
import { forkJoin, Observable } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';
import { AddInventoryDialogComponent } from '../../components/add-inventory-dialog/add-inventory-dialog.component';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [],
  templateUrl: './inventory.component.html',
  styleUrl: './inventory.component.css'
})
export class InventoryComponent implements OnInit {
  inventoryItems: Array<InventoryItem & { ingredient?: Ingredient }> = [];
  userId: string | null = null;

  constructor(
    private inventoryService: InventoryService,
    private ingredientService: IngredientService,
    private userService: UserService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.userService.getUserProfile().subscribe({
      next: (profile) => {
        this.userId = profile.id;
        this.loadInventory();
      },
      error: (err) => {
        console.error('Error fetching user profile:', err);
      }
    });
  }

  loadInventory() {
    if (!this.userId) return;

    this.inventoryService.getInventory(this.userId, { page: 0, size: 100 }).pipe(
      switchMap((response: InventoryListResponse) => {
        const items = response.content;
        const ingredientObservables: Observable<Ingredient>[] = items.map(item =>
          this.ingredientService.getIngredientById(item.ingredientId)
        );
        return forkJoin(ingredientObservables).pipe(
          map(ingredients => items.map((item, index) => ({
            ...item,
            ingredient: ingredients[index]
          })))
        );
      })
    ).subscribe({
      next: (items) => {
        this.inventoryItems = items;
      },
      error: (err) => {
        console.error('Error loading inventory:', err);
      }
    });
  }

  openAddInventoryDialog() {
    const dialogRef = this.dialog.open(AddInventoryDialogComponent, {
      width: '400px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.userId) {
        const request = {
          ingredientId: result.ingredientId,
          quantity: result.quantity
        };
        this.inventoryService.addInventoryIngredient(this.userId, request).subscribe({
          next: (response) => {
            this.ingredientService.getIngredientById(response.ingredientId).subscribe({
              next: (ingredient) => {
                this.inventoryItems.push({ ...response, ingredient });
              }
            });
          },
          error: (err) => {
            console.error('Error adding ingredient:', err);
          }
        });
      }
    });
  }

  openEditInventoryDialog(item: InventoryItem & { ingredient?: Ingredient }) {
    const dialogRef = this.dialog.open(AddInventoryDialogComponent, {
      width: '400px',
      data: {
        ingredientId: item.ingredientId,
        ingredientName: item.ingredient?.name,
        quantity: item.quantity,
        isEdit: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.userId) {
        const request = { quantity: result.quantity };
        this.inventoryService.editInventoryIngredient(this.userId, item.id, request).subscribe({
          next: (response) => {
            const index = this.inventoryItems.findIndex(i => i.id === item.id);
            if (index !== -1) {
              this.inventoryItems[index] = { ...this.inventoryItems[index], quantity: response.quantity };
            }
          },
          error: (err) => {
            console.error('Error editing ingredient:', err);
          }
        });
      }
    });
  }

  deleteInventoryItem(id: number) {
    if (this.userId) {
      this.inventoryService.deleteInventoryIngredient(this.userId, id).subscribe({
        next: () => {
          this.inventoryItems = this.inventoryItems.filter(item => item.id !== id);
        },
        error: (err) => {
          console.error('Error deleting ingredient:', err);
        }
      });
    }
  }
}