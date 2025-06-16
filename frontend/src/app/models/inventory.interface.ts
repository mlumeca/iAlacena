export interface InventoryListResponse {
  content: InventoryItem[]
  pageable: Pageable
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface InventoryItem {
  id: number
  userId: string
  ingredientId: number
  quantity: number
  addedAt: string
}

export interface Pageable {
  pageNumber: number
  pageSize: number
  offset: number
  paged: boolean
  unpaged: boolean
}

export interface PostRequest {
  ingredientId: number
  quantity: number
}

export interface EditRequest {
  quantity: number;
}

// response fot put and post
export interface InventoryResponse {
  id: number
  userId: string
  ingredientId: number
  quantity: number
  addedAt: string
}
