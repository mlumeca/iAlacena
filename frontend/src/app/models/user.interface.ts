export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  verifyPassword: string;
}

export interface RegisterResponse {
  id: string;
  username: string;
  token?: null;
  refreshToken?: null;
  avatar?: null;
}

export interface UserProfile {
  id: string;
  username: string;
  photoUrl?: string;
}
