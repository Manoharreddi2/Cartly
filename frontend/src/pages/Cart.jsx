import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShoppingBag, ArrowLeft, ArrowRight, Trash2, ShieldCheck, Tag } from 'lucide-react';
import { useCart } from '../context/CartContext';

const Cart = () => {
  const { cart, updateQuantity, removeFromCart, clearCart, loading } = useCart();
  const navigate = useNavigate();

  if (cart.items.length === 0) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-20 text-center">
        <div className="w-20 h-20 bg-emerald-50 text-emerald-600 rounded-3xl flex items-center justify-center mx-auto mb-6 shadow-sm">
          <ShoppingBag className="w-10 h-10" />
        </div>
        <h2 className="text-3xl font-black text-gray-900 tracking-tight mb-2">
          Your Cart is Empty
        </h2>
        <p className="text-sm text-gray-500 mb-8 max-w-sm mx-auto">
          Looks like you haven't added anything to your cart yet. Discover trending deals and fill it up!
        </p>
        <Link
          to="/products"
          className="inline-flex items-center gap-2 px-8 py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-sm rounded-2xl shadow-lg shadow-emerald-600/25 active:scale-95 transition-all"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Start Shopping</span>
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-4 border-b border-gray-200">
        <div>
          <h1 className="text-3xl font-black text-gray-900 tracking-tight">Shopping Cart</h1>
          <p className="text-sm text-gray-500 mt-1">
            You have <strong className="text-gray-900">{cart.totalItems}</strong> item(s) in your bag
          </p>
        </div>
        <button
          onClick={clearCart}
          className="flex items-center gap-1.5 text-xs font-semibold text-rose-600 hover:text-rose-700 hover:underline"
        >
          <Trash2 className="w-4 h-4" />
          <span>Clear Entire Cart</span>
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-10 items-start">
        {/* Cart Items List */}
        <div className="lg:col-span-8 space-y-4">
          {cart.items.map((item) => (
            <div
              key={item.id}
              className="bg-white rounded-2xl border border-gray-200/80 p-4 sm:p-6 shadow-sm flex flex-col sm:flex-row items-center gap-5 hover:border-gray-300 transition-all"
            >
              {/* Thumbnail */}
              <Link
                to={`/products/${item.productId}`}
                className="w-24 h-24 sm:w-28 sm:h-28 rounded-xl bg-gray-100 overflow-hidden shrink-0"
              >
                <img
                  src={item.productImage || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=200'}
                  alt={item.productName}
                  className="w-full h-full object-cover"
                />
              </Link>

              {/* Title & Info */}
              <div className="flex-1 min-w-0 text-center sm:text-left space-y-1">
                <Link
                  to={`/products/${item.productId}`}
                  className="font-bold text-gray-900 hover:text-emerald-600 text-base line-clamp-1 transition-colors"
                >
                  {item.productName}
                </Link>
                <div className="flex items-center justify-center sm:justify-start gap-2 text-sm">
                  <span className="font-extrabold text-emerald-700">
                    ${(item.discountedPrice || item.price)?.toFixed(2)}
                  </span>
                  {item.discountedPrice && item.discountedPrice < item.price && (
                    <span className="text-xs text-gray-400 line-through">
                      ${item.price?.toFixed(2)}
                    </span>
                  )}
                </div>
              </div>

              {/* Quantity Controls */}
              <div className="flex items-center border border-gray-200 rounded-xl bg-gray-50 overflow-hidden shrink-0">
                <button
                  onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                  className="px-3 py-1.5 text-gray-600 hover:bg-gray-200 transition-colors"
                >
                  -
                </button>
                <span className="px-3 py-1.5 text-xs font-bold text-gray-900 min-w-[2rem] text-center">
                  {item.quantity}
                </span>
                <button
                  onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                  className="px-3 py-1.5 text-gray-600 hover:bg-gray-200 transition-colors"
                >
                  +
                </button>
              </div>

              {/* Subtotal */}
              <div className="text-right shrink-0 min-w-[5rem]">
                <div className="text-base font-black text-gray-900">
                  ${item.itemTotal?.toFixed(2)}
                </div>
              </div>

              {/* Delete button */}
              <button
                onClick={() => removeFromCart(item.productId)}
                className="p-2 text-gray-400 hover:text-rose-600 rounded-lg hover:bg-rose-50 transition-colors"
                title="Remove item"
              >
                <Trash2 className="w-5 h-5" />
              </button>
            </div>
          ))}

          {/* Back to Shopping CTA */}
          <div className="pt-4">
            <Link
              to="/products"
              className="inline-flex items-center gap-2 text-sm font-bold text-emerald-600 hover:text-emerald-700"
            >
              <ArrowLeft className="w-4 h-4" />
              <span>Continue Shopping</span>
            </Link>
          </div>
        </div>

        {/* Order Summary Card */}
        <div className="lg:col-span-4 bg-white rounded-3xl border border-gray-200/80 p-6 sm:p-8 shadow-md space-y-6">
          <h2 className="text-lg font-black text-gray-900 tracking-tight pb-3 border-b border-gray-100">
            Order Summary
          </h2>

          <div className="space-y-3.5 text-sm">
            <div className="flex justify-between text-gray-600">
              <span>Cart Subtotal</span>
              <span className="font-semibold text-gray-900">${cart.subtotal?.toFixed(2)}</span>
            </div>

            {cart.discount > 0 && (
              <div className="flex justify-between text-emerald-600">
                <span className="flex items-center gap-1">
                  <Tag className="w-3.5 h-3.5" /> Product Discount
                </span>
                <span className="font-bold">-${cart.discount?.toFixed(2)}</span>
              </div>
            )}

            <div className="flex justify-between text-gray-600">
              <span>Delivery Charge</span>
              <span>
                {cart.deliveryFee === 0 ? (
                  <span className="font-bold text-emerald-600">FREE</span>
                ) : (
                  `$${cart.deliveryFee?.toFixed(2)}`
                )}
              </span>
            </div>

            <div className="flex justify-between text-gray-600">
              <span>Estimated Tax (5%)</span>
              <span className="font-semibold text-gray-900">${cart.tax?.toFixed(2)}</span>
            </div>

            <div className="pt-4 border-t border-gray-100 flex justify-between items-baseline">
              <span className="text-base font-extrabold text-gray-900">Total Amount</span>
              <span className="text-2xl font-black text-emerald-700">
                ${cart.total?.toFixed(2)}
              </span>
            </div>
          </div>

          <button
            onClick={() => navigate('/checkout')}
            className="w-full flex items-center justify-center gap-2 py-4 px-6 bg-emerald-600 hover:bg-emerald-700 active:scale-95 text-white font-extrabold text-sm rounded-2xl shadow-lg shadow-emerald-600/25 transition-all"
          >
            <span>Proceed to Checkout</span>
            <ArrowRight className="w-4 h-4" />
          </button>

          <div className="pt-2 flex items-center justify-center gap-2 text-xs text-gray-400">
            <ShieldCheck className="w-4 h-4 text-emerald-600" />
            <span>Encrypted SSL 256-Bit Checkout</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;
