export interface RecipeListResponse {
  totalElements: number
  totalPages: number
  pageNumber: number
  pageSize: number
  items: Recipe[]
}

export interface Recipe {
  id: number
  name: string
  description: string
  portions: number
  createdAt: string
  updatedAt: string
  imgUrl: string
  ingredients: Ingredient[]
  categories: Category[]
  userId: string
}

export interface Ingredient {
  id: number
  name: string
  quantity: number
  unitOfMeasure: string
}

export interface Category {
  id: number
  name: string
}

export interface CreateRecipeRequest {
  name: string
  description: string
  portions: number
  categoryIds: number[]
}

// RecipeListResponse = Recipe[]


// export interface RecipeDeleteRequest {
//   id: string;
// }