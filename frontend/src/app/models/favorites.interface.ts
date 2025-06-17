export interface Favorites {
  content: FavoriteItem[]
  pageable: Pageable
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface FavoriteItem {
  id: number
  userId: string
  recipeId: number
  addedAt: string
}

export interface Pageable {
  pageNumber: number
  pageSize: number
  offset: number
  paged: boolean
  unpaged: boolean
}

export interface NewFavoriteRequest {
  recipeId: number
}

export interface NewFavoriteResponse {
  id: number
  userId: string
  recipeId: number
  addedAt: string
}
