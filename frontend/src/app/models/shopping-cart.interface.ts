export interface ShoppingCart {
  content: ShoppingCartItem[]
  pageable: Pageable
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// Post Response
export interface ShoppingCartItem {
  id: number
  userId: string
  createdAt: string
  items: Item[]
}

export interface Item {
  ingredientId: number
  quantity: number
}

export interface Pageable {
  pageNumber: number
  pageSize: number
  offset: number
  paged: boolean
  unpaged: boolean
}

// Post Request
export interface addCartIngredient {
  ingredientId: number
}

export interface EditRequest {
  quantity: number;
}

export interface EditResponse {
  id: number
  shoppingCartId: number
  ingredientId: number
  quantity: number
}
