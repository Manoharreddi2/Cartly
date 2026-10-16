import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/authService';
import { useToast } from './ToastContext';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('cartly_token') || null);
  const [loading, setLoading] = useState(true);
  const { addToast } = useToast();

  useEffect(() => {
    const savedUser = localStorage.getItem('cartly_user');
    if (savedUser && token) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (e) {
        localStorage.removeItem('cartly_user');
      }
    }
    setLoading(false);
  }, [token]);

  const login = async (email, password) => {
    try {
      const res = await authService.login(email, password);
      const authData = res.data;
      setToken(authData.token);
      setUser(authData);
      localStorage.setItem('cartly_token', authData.token);
      localStorage.setItem('cartly_user', JSON.stringify(authData));
      addToast(`Welcome back, ${authData.name}!`, 'success');
      return authData;
    } catch (err) {
      addToast(err.message, 'error');
      throw err;
    }
  };

  const register = async (userData) => {
    try {
      const res = await authService.register(userData);
      const authData = res.data;
      setToken(authData.token);
      setUser(authData);
      localStorage.setItem('cartly_token', authData.token);
      localStorage.setItem('cartly_user', JSON.stringify(authData));
      addToast('Registration successful! Welcome to Cartly.', 'success');
      return authData;
    } catch (err) {
      addToast(err.message, 'error');
      throw err;
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('cartly_token');
    localStorage.removeItem('cartly_user');
    addToast('Logged out successfully', 'info');
  };

  const isAdmin = user?.roles?.includes('ROLE_ADMIN') || false;
  const isAuthenticated = !!token;

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated,
        isAdmin,
        loading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
