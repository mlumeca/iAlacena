import { Pipe, PipeTransform } from '@angular/core';
import { Category } from '../models/recipe.interface';

@Pipe({
  name: 'categories',
  standalone: true
})
export class CategoriesPipe implements PipeTransform {
  transform(categories: Category[]): string {
    return categories.map(c => c.name).join(', ') || 'Sin categorías';
  }
}
