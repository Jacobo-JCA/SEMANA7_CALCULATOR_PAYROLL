const GATEWAY_URL = import.meta.env.VITE_GATEWAY_URL || 'http://localhost:8080';

export const apiFetch = async (endpoint: string, options: RequestInit = {}) => {
  const token = localStorage.getItem('accessToken');
  if (!token) {
    throw new Error('No access token found. Please login first.');
  }

  const response = await fetch(`${GATEWAY_URL}${endpoint}`, {
    ...options,
    headers: {
      ...options.headers,
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  // Si el token expiró o es inválido, redirige al login
  if (response.status === 401) {
    localStorage.removeItem('accessToken');
    throw new Error('Unauthorized. Please login again.');
  }

  return response;
};
