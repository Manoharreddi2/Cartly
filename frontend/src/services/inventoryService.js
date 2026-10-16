import api from './api';

export const inventoryService = {
  getInventory: (productId) => api.get(`/inventory/${productId}`),
  getAll: () => api.get('/inventory'),
  getLowStock: () => api.get('/inventory/low-stock'),
  updateStock: (productId, quantity, threshold) =>
    api.put(`/inventory/${productId}`, { quantity, threshold }),
};
