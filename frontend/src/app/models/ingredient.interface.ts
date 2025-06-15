export interface IngredientList {
  totalElements: number
  totalPages: number
  pageNumber: number
  pageSize: number
  items: Ingredient[]
}

export interface Ingredient {
  id: number
  name: string
  quantity: number
  unitOfMeasure: string
  categories: Category[]
}

export interface Category {
  id: number
  name: string
}

export interface PostRequest {
  name: string
  unitOfMeasure: string
}

export interface PutRequest {
  name: string
  categoryIds: number[]
}

export interface IngredientResponse {
  id: number
  name: string
  quantity: number
  unitOfMeasure: string
}
