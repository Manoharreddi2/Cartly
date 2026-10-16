import React from 'react';
import { Link } from 'react-router-dom';
import { ShoppingCart, Heart, Star } from 'lucide-react';
import { useCart } from '../../context/CartContext';
import { useWishlist } from '../../context/WishlistContext';

const ProductCard = ({ product }) => {
  const { addToCart } = useCart();
  const { isInWishlist, addToWishlist, removeFromWishlist } = useWishlist();

  const inWishlist = isInWishlist(product.id);

  const handleWishlistClick = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (inWishlist) {
      removeFromWishlist(product.id);
    } else {
      addToWishlist(product);
    }
  };

  const handleAddToCart = (e) => {
    e.preventDefault();
    e.stopPropagation();
    addToCart(product, 1);
  };

  return (
    <div className="group relative bg-white rounded-2xl border border-gray-200/80 hover:border-emerald-500/50 shadow-sm hover:shadow-xl transition-all duration-300 flex flex-col overflow-hidden">
      {/* Product Image */}
      <Link to={`/products/${product.id}`} className="relative block aspect-square bg-gray-100 overflow-hidden">
        <img
          src={product.imageUrl || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500'}
          alt={product.name}
          className="w-full h-full object-cover object-center group-hover:scale-105 transition-transform duration-500"
          loading="lazy"
        />

        {/* Discount Badge */}
        {product.discount > 0 && (
          <span className="absolute top-3 left-3 px-2.5 py-1 text-xs font-black bg-rose-500 text-white rounded-lg shadow-sm">
            {product.discount}% OFF
          </span>
        )}

        {/* Wishlist Button */}
        <button
          onClick={handleWishlistClick}
          className={`absolute top-3 right-3 p-2 rounded-full transition-all shadow-md ${
            inWishlist
              ? 'bg-rose-50 text-rose-600'
              : 'bg-white/90 text-gray-500 hover:text-rose-500 hover:bg-white'
          }`}
          title={inWishlist ? 'Remove from Wishlist' : 'Add to Wishlist'}
        >
          <Heart className={`w-4 h-4 ${inWishlist ? 'fill-current' : ''}`} />
        </button>
      </Link>

      {/* Content */}
      <div className="p-4 sm:p-5 flex-1 flex flex-col justify-between">
        <div>
          <div className="flex items-center justify-between text-xs text-gray-500 mb-1.5">
            <span className="font-semibold uppercase tracking-wider text-emerald-600">
              {product.brand || product.category}
            </span>
            <div className="flex items-center gap-1 font-semibold text-gray-700">
              <Star className="w-3.5 h-3.5 fill-amber-400 text-amber-400" />
              <span>{product.rating?.toFixed(1) || '4.5'}</span>
              <span className="text-gray-400">({product.reviewCount || 12})</span>
            </div>
          </div>

          <Link to={`/products/${product.id}`} className="block group-hover:text-emerald-600 transition-colors">
            <h3 className="font-bold text-gray-900 line-clamp-1 text-base">{product.name}</h3>
          </Link>
          <p className="text-xs text-gray-500 line-clamp-2 mt-1 mb-3">
            {product.description}
          </p>
        </div>

        {/* Price and Cart button */}
        <div className="pt-3 border-t border-gray-100 flex items-center justify-between">
          <div>
            <div className="text-lg font-black text-gray-900">
              ${(product.discountedPrice || product.price)?.toFixed(2)}
            </div>
            {product.discount > 0 && (
              <div className="text-xs text-gray-400 line-through">
                ${product.price?.toFixed(2)}
              </div>
            )}
          </div>

          <button
            onClick={handleAddToCart}
            className="flex items-center gap-1.5 px-3.5 py-2 text-xs font-bold text-white bg-emerald-600 hover:bg-emerald-700 active:scale-95 rounded-xl shadow-sm transition-all"
            title="Add to Cart"
          >
            <ShoppingCart className="w-4 h-4" />
            <span className="hidden sm:inline">Add</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductCard;
