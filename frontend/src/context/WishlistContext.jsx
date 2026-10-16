import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { cartService } from '../services/cartService';
import { useAuth } from './AuthContext';
import { useCart } from './CartContext';
import { useToast } from './ToastContext';

const WishlistContext = createContext(null);

export const WishlistProvider = ({ children }) => {
  const [wishlist, setWishlist] = useState([]);
  const [loading, setLoading] = useState(false);
  const { isAuthenticated } = useAuth();
  const { fetchCart } = useCart();
  const { addToast } = useToast();

  const fetchWishlist = useCallback(async () => {
    try {
      setLoading(true);
      const res = await cartService.getWishlist();
      if (res.data) {
        setWishlist(res.data);
      }
    } catch (err) {
      console.warn('Could not load wishlist:', err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchWishlist();
  }, [fetchWishlist, isAuthenticated]);

  const isInWishlist = (productId) => {
    return wishlist.some((item) => item.productId === productId);
  };

  const addToWishlist = async (product) => {
    try {
      const res = await cartService.addToWishlist({
        productId: product.id,
        productName: product.name,
        productImage: product.imageUrl,
        price: product.price,
        discountedPrice: product.discountedPrice || product.price,
        category: product.category,
      });
      if (res.data) {
        setWishlist((prev) => [res.data, ...prev.filter((i) => i.productId !== product.id)]);
      }
      addToast(`Added "${product.name}" to wishlist!`, 'success');
    } catch (err) {
      addToast(err.message, 'error');
    }
  };

  const removeFromWishlist = async (productId) => {
    try {
      await cartService.removeFromWishlist(productId);
      setWishlist((prev) => prev.filter((item) => item.productId !== productId));
      addToast('Removed from wishlist', 'info');
    } catch (err) {
      addToast(err.message, 'error');
    }
  };

  const moveToCart = async (productId) => {
    try {
      await cartService.moveToCart(productId);
      setWishlist((prev) => prev.filter((item) => item.productId !== productId));
      await fetchCart();
      addToast('Item moved to shopping cart!', 'success');
    } catch (err) {
      addToast(err.message, 'error');
    }
  };

  return (
    <WishlistContext.Provider
      value={{
        wishlist,
        loading,
        fetchWishlist,
        isInWishlist,
        addToWishlist,
        removeFromWishlist,
        moveToCart,
      }}
    >
      {children}
    </WishlistContext.Provider>
  );
};

export const useWishlist = () => useContext(WishlistContext);
