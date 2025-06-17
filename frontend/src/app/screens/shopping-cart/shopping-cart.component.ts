import { Component, OnInit } from '@angular/core';
import { ShoppingCartService } from '../../services/shopping-cart.service';
import { UserService } from '../../services/user.service';
import { addCartIngredient, EditRequest, ShoppingCart, ShoppingCartItem } from '../../models/shopping-cart.interface';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { AddShoppingCartDialogComponent } from '../../components/add-shopping-cart-dialog/add-shopping-cart-dialog.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-shopping-cart',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './shopping-cart.component.html',
  styleUrl: './shopping-cart.component.css'
})

export class ShoppingCartComponent implements OnInit {
  shoppingCartItems: Array<ShoppingCartItem> = [];
  userId: string | null = null;

  constructor(
    private shoppingCartService: ShoppingCartService,
    private userService: UserService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.userService.getUserProfile(localStorage.getItem('id') || '').subscribe({
      next: (profile) => {
        this.userId = profile.id;
        this.loadShoppingCart();
      },
      error: (err) => {
        console.error('Error fetching user profile:', err);
      }
    });
  }

  loadShoppingCart() {
    if (!this.userId) return;

    this.shoppingCartService.getShoppingCart(this.userId).subscribe({
      next: (response: ShoppingCart) => {
        this.shoppingCartItems = response.content;
      },
      error: (err) => {
        console.error('Error loading shopping cart:', err);
      }
    });
  }

  openAddShoppingCartDialog() {
    const dialogRef = this.dialog.open(AddShoppingCartDialogComponent, {
      width: '400px',
      data: { isEdit: false } // Default data for add mode
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.userId) {
        const request: addCartIngredient = {
          ingredientId: result.ingredientId
        };
        this.shoppingCartService.addShoppingCartIngredient(this.userId, request).subscribe({
          next: (response) => {
            this.shoppingCartItems.push(response);
          },
          error: (err) => {
            console.error('Error adding ingredient to shopping cart:', err);
          }
        });
      }
    });
  }

  openEditShoppingCartDialog(item: ShoppingCartItem) {
    const dialogRef = this.dialog.open(AddShoppingCartDialogComponent, {
      width: '400px',
      data: {
        ingredientId: item.items[0]?.ingredientId,
        quantity: item.items[0]?.quantity,
        isEdit: true
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.userId) {
        const request: EditRequest = { quantity: result.quantity };
        this.shoppingCartService.editShoppingCartIngredient(this.userId, item.id, request).subscribe({
          next: (response) => {
            const index = this.shoppingCartItems.findIndex(i => i.id === item.id);
            if (index !== -1) {
              this.shoppingCartItems[index] = { ...this.shoppingCartItems[index], items: [{ ...item.items[0], quantity: response.quantity }] };
            }
          },
          error: (err) => {
            console.error('Error editing ingredient in shopping cart:', err);
          }
        });
      }
    });
  }

  deleteShoppingCartItem(id: number) {
    if (this.userId) {
      this.shoppingCartService.deleteShoppingCartIngredient(this.userId, id).subscribe({
        next: () => {
          this.shoppingCartItems = this.shoppingCartItems.filter(item => item.id !== id);
        },
        error: (err) => {
          console.error('Error deleting ingredient from shopping cart:', err);
        }
      });
    }
  }
}