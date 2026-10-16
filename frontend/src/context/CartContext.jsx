import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { cartService } from '../services/cartService';
import { useAuth } from './AuthContext';
import { useToast } from './ToastContext';

const CartContext = createContext(null);

export const CartProvider = ({ children }) => {
  const [cart, setCart] = useState({
    items: [],
    totalItems: 0,
    subtotal: 0,
    discount: 0,
    deliveryFee: 0,
    tax: 0,
    total: 0,
  });
  const [loading, setLoading] = useState(false);
  const { isAuthenticated } = useAuth();
  const { addToast } = useToast();

  const fetchCart = useCallback(async () => {
    try {
      setLoading(true);
      const res = await cartService.getCart();
      if (res.data) {
        setCart(res.data);
      }
    } catch (err) {
      console.warn('Could not load cart:', err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchCart();
  }, [fetchCart, isAuthenticated]);

  const addToCart = async (product, quantity = 1) => {
    try {
      const res = await cartService.addItem({
        productId: product.id,
        productName: product.name,
        productImage: product.imageUrl,
        price: product.price,
        discountedPrice: product.discountedPrice || product.price,
        quantity,
      });
      if (res.data) setCart(res.data);
      addToast(`Added "${product.name}" to your cart!`, 'success');
    } catch (err) {
      addToast(err.message, 'error');
    }
  };

  const updateQuantity = async (productId, quantity) => {
    try {
      const res = await cartService.updateQuantity(productId, quantity);
      if (res.data) setCart(res.data);
    } catch (err) {
      addToast(err.message, 'error');
    }
  };

  const removeFromCart = async (productId) => {
    try {
      const res = await cartService.removeItem(productId);
      if (res.data) setCart(res.data);
      addToast('Item removed from cart', 'info');
    } catch (err) {
      addToast(err.message, 'error');
    }
  };

  const clearCart = async () => {
    try {
      const res = await cartService.clearCart();
      if (res.data) setCart(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <CartContext.Provider
      value={{
        cart,
        loading,
        fetchCart,
        addToCart,
        updateQuantity,
        removeFromCart,
        clearCart,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => useContext(CartContext);
