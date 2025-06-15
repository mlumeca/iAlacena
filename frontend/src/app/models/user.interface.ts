export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  verifyPassword: string;
}

export interface RegisterResponse {
  id: string;
  username: string;
  token?: string;
  refreshToken?: string;
  avatar?: string;
}

export interface UserProfile {
  id: string;
  username: string;
  photoUrl?: string;
}

export interface EditProfileRequest {
  username: string;
  email: string;
}

export interface EditRoleRequest {
  role: string;
}

export interface EditPasswordRequest {
  oldPassword: string;
  newPassword: string;
}

// response for profile, role, password edit
export interface EditProfileResponse {
  id: string;
  username: string;
  token?: string;
  refreshToken?: string;
}

export interface PostAvatarRequest {
  file: string;
}

export interface PostAvatarResponse {
  id: string;
  name: string;
  uri: string;
  type: string;
  size: number;
}

export interface ResetPasswordRequest {
  token: string;
  password: string;
}

export interface ForgotPasswordRequest {
  username: string;
}

export interface ProfileListResponse {
  content: Content[]
  pageable: Pageable
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface Content {
  id: string
  username: string
  token: any
  refreshToken: any
}

export interface Pageable {
  pageNumber: number
  pageSize: number
  offset: number
  paged: boolean
  unpaged: boolean
}