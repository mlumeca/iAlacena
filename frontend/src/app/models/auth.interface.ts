export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  id: string;
  username: string;
  token: string;
  refreshToken: string;
}

export interface LogoutRequest {
  refreshToken: string;
}