const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080';

export interface LoginParams {
  username?: string;
  password?: string;
}

export interface RegisterParams extends LoginParams { }

export interface AuthResponse {
  accessToken: string;
  userId: number;
  username: string;
  role: string;
}

export const login = async (credentials: LoginParams): Promise<AuthResponse> => {
  const response = await fetch(`${GATEWAY_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  });

  if (!response.ok) {
    throw new Error('Invalid credentials');
  }

  const data: AuthResponse = await response.json();
  localStorage.setItem('accessToken', data.accessToken);

  return data;
};

export const register = async (credentials: RegisterParams): Promise<void> => {
  const response = await fetch(`${GATEWAY_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(credentials),
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || 'Failed to register user');
  }
};