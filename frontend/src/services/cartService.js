import api from './api';

export const cartService = {
  getCart: () => api.get('/cart'),
  addItem: (item) => api.post('/cart/items', item),
  updateQuantity: (productId, quantity) => api.put(`/cart/items/${productId}`, { quantity }),
  removeItem: (productId) => api.delete(`/cart/items/${productId}`),
  clearCart: () => api.delete('/cart'),

  // Wishlist
  getWishlist: () => api.get('/cart/wishlist'),
  addToWishlist: (item) => api.post('/cart/wishlist', item),
  removeFromWishlist: (productId) => api.delete(`/cart/wishlist/${productId}`),
  moveToCart: (productId) => api.post(`/cart/wishlist/${productId}/move-to-cart`),
};
