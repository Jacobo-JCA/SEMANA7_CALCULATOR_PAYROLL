import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { login, type LoginParams, type AuthResponse } from '../services/authService';
import { useNavigate } from 'react-router-dom';

interface AuthContextType {
  isAuthenticated: boolean;
  token: string | null;
  loginUser: (credentials: LoginParams) => Promise<void>;
  logoutUser: () => void;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(localStorage.getItem('accessToken'));
  const [isLoading, setIsLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    // Optionally trigger an initial check to validate token format/expiry here
    setIsLoading(false);

    const handleAuthError = () => {
      logoutUser();
    };

    window.addEventListener('auth-error', handleAuthError);
    return () => window.removeEventListener('auth-error', handleAuthError);
  }, []);

  const loginUser = async (credentials: LoginParams) => {
    const response: AuthResponse = await login(credentials);
    const newToken = response.accessToken;
    setToken(newToken);
    localStorage.setItem('accessToken', newToken);
    navigate('/employees');
  };

  const logoutUser = () => {
    setToken(null);
    localStorage.removeItem('accessToken');
    navigate('/login');
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated: !!token, token, loginUser, logoutUser, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
