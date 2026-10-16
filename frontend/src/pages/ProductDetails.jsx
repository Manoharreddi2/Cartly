import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  ShoppingCart,
  Heart,
  Star,
  Truck,
  ShieldCheck,
  RotateCcw,
  Zap,
  Check,
  ChevronRight,
  AlertCircle
} from 'lucide-react';
import { productService } from '../services/productService';
import { useCart } from '../context/CartContext';
import { useWishlist } from '../context/WishlistContext';
import ProductGrid from '../components/product/ProductGrid';

const ProductDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { addToCart } = useCart();
  const { isInWishlist, addToWishlist, removeFromWishlist } = useWishlist();

  const [product, setProduct] = useState(null);
  const [relatedProducts, setRelatedProducts] = useState([]);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await productService.getProductById(id);
        if (res.data) {
          setProduct(res.data);
          // Fetch related products
          const relRes = await productService.getProducts({
            category: res.data.category,
            size: 4,
          });
          if (relRes.data?.content) {
            setRelatedProducts(relRes.data.content.filter((p) => p.id !== res.data.id));
          }
        }
      } catch (err) {
        setError(err.message || 'Product not found');
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
    setQuantity(1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }, [id]);

  if (loading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16 animate-pulse">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
          <div className="aspect-square bg-gray-200 rounded-3xl"></div>
          <div className="space-y-4">
            <div className="h-6 bg-gray-200 rounded w-1/4"></div>
            <div className="h-10 bg-gray-200 rounded w-3/4"></div>
            <div className="h-6 bg-gray-200 rounded w-1/3"></div>
            <div className="h-24 bg-gray-200 rounded"></div>
            <div className="h-12 bg-gray-200 rounded w-1/2"></div>
          </div>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="max-w-md mx-auto my-20 p-8 text-center bg-white rounded-3xl border border-gray-200 shadow-sm space-y-4">
        <AlertCircle className="w-12 h-12 text-rose-500 mx-auto" />
        <h2 className="text-xl font-bold text-gray-900">{error || 'Product Not Found'}</h2>
        <p className="text-sm text-gray-500">The product you are looking for may have been removed or does not exist.</p>
        <Link
          to="/products"
          className="inline-block px-6 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm rounded-xl transition-all"
        >
          Back to Catalog
        </Link>
      </div>
    );
  }

  const inWishlist = isInWishlist(product.id);

  const handleAddToCart = () => {
    addToCart(product, quantity);
  };

  const handleBuyNow = () => {
    addToCart(product, quantity);
    navigate('/checkout');
  };

  const handleWishlistToggle = () => {
    if (inWishlist) {
      removeFromWishlist(product.id);
    } else {
      addToWishlist(product);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-16">
      {/* Breadcrumb Navigation */}
      <nav className="flex items-center gap-2 text-xs font-semibold text-gray-500">
        <Link to="/" className="hover:text-emerald-600 transition-colors">Home</Link>
        <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
        <Link to="/products" className="hover:text-emerald-600 transition-colors">Products</Link>
        <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
        <Link
          to={`/products?category=${encodeURIComponent(product.category)}`}
          className="hover:text-emerald-600 transition-colors"
        >
          {product.category}
        </Link>
        <ChevronRight className="w-3.5 h-3.5 text-gray-400" />
        <span className="text-gray-900 truncate max-w-xs">{product.name}</span>
      </nav>

      {/* Main Product Showcase */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-start">
        {/* Gallery Preview */}
        <div className="lg:col-span-6 space-y-4">
          <div className="relative aspect-square bg-white rounded-3xl border border-gray-200/80 overflow-hidden shadow-sm">
            <img
              src={product.imageUrl || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800'}
              alt={product.name}
              className="w-full h-full object-cover object-center"
            />
            {product.discount > 0 && (
              <span className="absolute top-4 left-4 px-3 py-1 text-xs font-black bg-rose-500 text-white rounded-xl shadow-md">
                {product.discount}% OFF
              </span>
            )}
          </div>
        </div>

        {/* Product Details & Actions */}
        <div className="lg:col-span-6 space-y-6">
          <div>
            <div className="flex items-center justify-between mb-2">
              <span className="px-3 py-1 rounded-full bg-emerald-50 text-emerald-700 font-bold text-xs uppercase tracking-wider">
                {product.brand || product.category}
              </span>
              <div className="flex items-center gap-1.5 font-bold text-sm text-gray-800">
                <Star className="w-4 h-4 fill-amber-400 text-amber-400" />
                <span>{product.rating?.toFixed(1) || '4.5'}</span>
                <span className="text-gray-400 font-normal">({product.reviewCount || 24} customer reviews)</span>
              </div>
            </div>

            <h1 className="text-3xl sm:text-4xl font-black text-gray-900 tracking-tight leading-tight">
              {product.name}
            </h1>
          </div>

          {/* Pricing */}
          <div className="p-4 bg-emerald-50/50 rounded-2xl border border-emerald-100 flex items-center justify-between">
            <div>
              <div className="flex items-baseline gap-3">
                <span className="text-3xl font-black text-emerald-800">
                  ${(product.discountedPrice || product.price)?.toFixed(2)}
                </span>
                {product.discount > 0 && (
                  <span className="text-base text-gray-400 line-through">
                    ${product.price?.toFixed(2)}
                  </span>
                )}
              </div>
              <p className="text-xs text-emerald-700 font-medium mt-0.5">
                Inclusive of all sales taxes. Free delivery on orders $100+.
              </p>
            </div>

            {/* Stock status badge */}
            <div>
              {product.stock > 10 ? (
                <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
                  <Check className="w-3.5 h-3.5" /> In Stock ({product.stock})
                </span>
              ) : product.stock > 0 ? (
                <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold bg-amber-100 text-amber-800">
                  <Zap className="w-3.5 h-3.5" /> Only {product.stock} left!
                </span>
              ) : (
                <span className="inline-flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold bg-rose-100 text-rose-800">
                  Out of Stock
                </span>
              )}
            </div>
          </div>

          {/* Description */}
          <div>
            <h3 className="text-sm font-bold text-gray-900 uppercase tracking-wider mb-2">Description</h3>
            <p className="text-sm text-gray-600 leading-relaxed whitespace-pre-line">
              {product.description}
            </p>
          </div>

          {/* Quantity Selector & Action Buttons */}
          <div className="space-y-4 pt-4 border-t border-gray-200">
            <div className="flex items-center gap-4">
              <span className="text-xs font-bold uppercase tracking-wider text-gray-500">Quantity:</span>
              <div className="inline-flex items-center border border-gray-200 rounded-xl bg-white shadow-sm overflow-hidden">
                <button
                  onClick={() => setQuantity((prev) => Math.max(1, prev - 1))}
                  disabled={quantity <= 1}
                  className="px-3.5 py-2 text-gray-600 hover:bg-gray-100 disabled:opacity-40 transition-colors"
                >
                  -
                </button>
                <span className="px-4 py-2 text-sm font-bold text-gray-900 min-w-[2.5rem] text-center">
                  {quantity}
                </span>
                <button
                  onClick={() => setQuantity((prev) => Math.min(product.stock || 99, prev + 1))}
                  disabled={quantity >= product.stock}
                  className="px-3.5 py-2 text-gray-600 hover:bg-gray-100 disabled:opacity-40 transition-colors"
                >
                  +
                </button>
              </div>
            </div>

            <div className="flex flex-col sm:flex-row items-center gap-3 pt-2">
              <button
                onClick={handleAddToCart}
                disabled={product.stock <= 0}
                className="w-full sm:flex-1 flex items-center justify-center gap-2 px-6 py-3.5 bg-emerald-600 hover:bg-emerald-700 disabled:bg-gray-300 text-white font-extrabold text-sm rounded-2xl shadow-lg shadow-emerald-600/25 active:scale-95 transition-all"
              >
                <ShoppingCart className="w-5 h-5" />
                <span>Add to Cart</span>
              </button>

              <button
                onClick={handleBuyNow}
                disabled={product.stock <= 0}
                className="w-full sm:flex-1 flex items-center justify-center gap-2 px-6 py-3.5 bg-slate-900 hover:bg-slate-800 disabled:bg-gray-300 text-white font-extrabold text-sm rounded-2xl shadow-lg active:scale-95 transition-all"
              >
                <Zap className="w-5 h-5 text-emerald-400" />
                <span>Buy Now</span>
              </button>

              <button
                onClick={handleWishlistToggle}
                className={`p-3.5 rounded-2xl border transition-all ${
                  inWishlist
                    ? 'border-rose-300 bg-rose-50 text-rose-600 shadow-sm'
                    : 'border-gray-200 hover:bg-gray-50 text-gray-600'
                }`}
                title={inWishlist ? 'Remove from Wishlist' : 'Add to Wishlist'}
              >
                <Heart className={`w-5 h-5 ${inWishlist ? 'fill-current' : ''}`} />
              </button>
            </div>
          </div>

          {/* Guarantees Box */}
          <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pt-6 border-t border-gray-100 text-xs text-gray-600">
            <div className="flex items-center gap-2 p-3 bg-gray-50 rounded-xl">
              <Truck className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>Fast 2-3 Day Delivery</span>
            </div>
            <div className="flex items-center gap-2 p-3 bg-gray-50 rounded-xl">
              <RotateCcw className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>30-Day Hassle-Free Returns</span>
            </div>
            <div className="flex items-center gap-2 p-3 bg-gray-50 rounded-xl">
              <ShieldCheck className="w-4 h-4 text-emerald-600 shrink-0" />
              <span>1-Year Official Warranty</span>
            </div>
          </div>
        </div>
      </div>

      {/* Related Products */}
      {relatedProducts.length > 0 && (
        <section className="pt-12 border-t border-gray-200">
          <h2 className="text-2xl font-black text-gray-900 tracking-tight mb-8">
            You Might Also Like
          </h2>
          <ProductGrid products={relatedProducts} />
        </section>
      )}
    </div>
  );
};

export default ProductDetails;
