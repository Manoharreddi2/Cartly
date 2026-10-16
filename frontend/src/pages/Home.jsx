import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  ArrowRight,
  TrendingUp,
  Sparkles,
  Award,
  Zap,
  Tag,
  ShieldCheck,
  ChevronRight
} from 'lucide-react';
import ProductGrid from '../components/product/ProductGrid';
import { productService } from '../services/productService';
import { categoryService } from '../services/categoryService';

const Home = () => {
  const [categories, setCategories] = useState([]);
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const [bestSellers, setBestSellers] = useState([]);
  const [newArrivals, setNewArrivals] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadHomeData = async () => {
      try {
        setLoading(true);
        const [catsRes, featRes, bestRes, newRes] = await Promise.all([
          categoryService.getCategories(true),
          productService.getFeatured(),
          productService.getBestSellers(),
          productService.getNewArrivals(),
        ]);

        if (catsRes.data) setCategories(catsRes.data);
        if (featRes.data) setFeaturedProducts(featRes.data);
        if (bestRes.data) setBestSellers(bestRes.data);
        if (newRes.data) setNewArrivals(newRes.data);
      } catch (err) {
        console.error('Error loading home data:', err);
      } finally {
        setLoading(false);
      }
    };

    loadHomeData();
  }, []);

  return (
    <div className="space-y-16 pb-20">
      {/* Hero Section */}
      <section className="relative overflow-hidden bg-gradient-to-br from-slate-900 via-slate-800 to-emerald-950 text-white py-16 sm:py-24 rounded-3xl mx-4 sm:mx-6 lg:mx-8 mt-4 shadow-2xl">
        {/* Abstract background blobs */}
        <div className="absolute top-0 right-0 -mt-12 -mr-12 w-96 h-96 bg-emerald-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div className="absolute bottom-0 left-0 -mb-12 -ml-12 w-80 h-80 bg-teal-500/10 rounded-full blur-3xl pointer-events-none"></div>

        <div className="relative max-w-7xl mx-auto px-6 sm:px-8 lg:px-12 grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
          <div className="lg:col-span-7 space-y-6 text-center lg:text-left">
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-emerald-500/20 border border-emerald-500/30 text-emerald-300 text-xs font-bold uppercase tracking-wider">
              <Sparkles className="w-4 h-4 text-emerald-400" />
              <span>Modern E-Commerce Experience</span>
            </div>

            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-black tracking-tight leading-tight">
              Everything You Need, <br className="hidden sm:block" />
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-400 via-teal-300 to-cyan-400">
                Just a Cart Away.
              </span>
            </h1>

            <p className="text-base sm:text-lg text-gray-300 max-w-xl mx-auto lg:mx-0 leading-relaxed">
              Explore flagship smartphones, powerful laptops, trendsetting fashion, footwear, and home essentials. Fast delivery, verified sellers, and effortless shopping.
            </p>

            <div className="flex flex-col sm:flex-row items-center justify-center lg:justify-start gap-4 pt-2">
              <Link
                to="/products"
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2.5 px-8 py-3.5 rounded-2xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-extrabold text-sm shadow-lg shadow-emerald-500/25 hover:shadow-emerald-500/40 hover:-translate-y-0.5 active:translate-y-0 transition-all"
              >
                <span>Shop Catalog</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
              <Link
                to="/products?category=Electronics"
                className="w-full sm:w-auto inline-flex items-center justify-center gap-2 px-6 py-3.5 rounded-2xl bg-white/10 hover:bg-white/15 text-white font-semibold text-sm border border-white/15 transition-all"
              >
                <span>Explore Electronics</span>
                <ChevronRight className="w-4 h-4 text-gray-400" />
              </Link>
            </div>

            {/* Micro proof badges */}
            <div className="pt-6 border-t border-slate-700/60 grid grid-cols-3 gap-4 text-center lg:text-left max-w-lg mx-auto lg:mx-0">
              <div>
                <p className="text-xl sm:text-2xl font-black text-white">100%</p>
                <p className="text-xs text-gray-400 font-medium">Genuine Products</p>
              </div>
              <div>
                <p className="text-xl sm:text-2xl font-black text-white">24/7</p>
                <p className="text-xs text-gray-400 font-medium">Customer Support</p>
              </div>
              <div>
                <p className="text-xl sm:text-2xl font-black text-white">30 Days</p>
                <p className="text-xs text-gray-400 font-medium">Easy Returns</p>
              </div>
            </div>
          </div>

          {/* Hero Feature Visual */}
          <div className="lg:col-span-5 relative flex justify-center">
            <div className="relative w-full max-w-sm">
              <div className="absolute inset-0 bg-gradient-to-tr from-emerald-500 to-teal-400 rounded-3xl rotate-3 opacity-20 filter blur-xl"></div>
              <div className="relative bg-slate-800/90 border border-slate-700 rounded-3xl p-6 shadow-2xl backdrop-blur-sm space-y-4">
                <div className="aspect-square bg-slate-900 rounded-2xl overflow-hidden relative group">
                  <img
                    src="https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=600"
                    alt="iPhone 17"
                    className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                  />
                  <span className="absolute top-3 left-3 px-3 py-1 bg-rose-500 text-white font-black text-xs rounded-lg shadow-md">
                    HOT DEAL
                  </span>
                </div>
                <div className="flex justify-between items-start">
                  <div>
                    <span className="text-xs text-emerald-400 font-bold uppercase tracking-wider">Featured Flagship</span>
                    <h3 className="text-lg font-bold text-white">Apple iPhone 17</h3>
                  </div>
                  <div className="text-right">
                    <p className="text-lg font-black text-emerald-400">$1079.10</p>
                    <p className="text-xs text-gray-400 line-through">$1199.00</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Popular Categories */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
              <Tag className="w-4 h-4" />
              <span>Browse by Department</span>
            </div>
            <h2 className="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
              Popular Categories
            </h2>
          </div>
          <Link
            to="/products"
            className="text-sm font-bold text-emerald-600 hover:text-emerald-700 flex items-center gap-1 group"
          >
            <span>All Categories</span>
            <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>

        <div className="grid grid-cols-2 sm:grid-cols-4 lg:grid-cols-8 gap-4">
          {categories.map((cat) => (
            <Link
              key={cat.id}
              to={`/products?category=${encodeURIComponent(cat.name)}`}
              className="group bg-white rounded-2xl p-4 text-center border border-gray-200/80 hover:border-emerald-500/50 shadow-sm hover:shadow-md transition-all flex flex-col items-center"
            >
              <div className="w-14 h-14 rounded-full bg-emerald-50 text-emerald-600 flex items-center justify-center overflow-hidden mb-3 group-hover:scale-110 transition-transform">
                <img
                  src={cat.imageUrl}
                  alt={cat.name}
                  className="w-full h-full object-cover"
                />
              </div>
              <span className="text-xs font-bold text-gray-800 group-hover:text-emerald-600 transition-colors line-clamp-1">
                {cat.name}
              </span>
            </Link>
          ))}
        </div>
      </section>

      {/* Featured Products */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
              <Sparkles className="w-4 h-4" />
              <span>Handpicked for You</span>
            </div>
            <h2 className="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
              Featured Products
            </h2>
          </div>
          <Link
            to="/products"
            className="text-sm font-bold text-emerald-600 hover:text-emerald-700 flex items-center gap-1 group"
          >
            <span>View All</span>
            <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>

        <ProductGrid products={featuredProducts.slice(0, 8)} loading={loading} />
      </section>

      {/* Promotional Discount Banner */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="bg-gradient-to-r from-emerald-600 via-teal-600 to-emerald-800 rounded-3xl p-8 sm:p-12 text-white shadow-xl flex flex-col md:flex-row items-center justify-between gap-8">
          <div className="space-y-3 text-center md:text-left">
            <span className="px-3 py-1 bg-white/20 text-white rounded-full text-xs font-bold uppercase tracking-wider">
              Limited Time Deals
            </span>
            <h3 className="text-2xl sm:text-4xl font-black">
              Save Up to 25% on Premium Electronics & Audio
            </h3>
            <p className="text-sm sm:text-base text-emerald-100 max-w-xl">
              Upgrade your lifestyle with top brands like Sony, Apple, Samsung, and Dell. No coupon code needed at checkout.
            </p>
          </div>
          <Link
            to="/products?category=Electronics"
            className="shrink-0 px-8 py-3.5 bg-white text-emerald-800 hover:bg-emerald-50 rounded-2xl font-black text-sm shadow-md hover:scale-105 active:scale-95 transition-all"
          >
            Claim Deals Now
          </Link>
        </div>
      </section>

      {/* Best Sellers */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
              <Award className="w-4 h-4" />
              <span>Customer Favorites</span>
            </div>
            <h2 className="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
              Best Sellers
            </h2>
          </div>
          <Link
            to="/products?sortBy=rating"
            className="text-sm font-bold text-emerald-600 hover:text-emerald-700 flex items-center gap-1 group"
          >
            <span>Top Rated</span>
            <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>

        <ProductGrid products={bestSellers.slice(0, 8)} loading={loading} />
      </section>

      {/* New Arrivals */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between mb-8">
          <div>
            <div className="flex items-center gap-2 text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
              <Zap className="w-4 h-4" />
              <span>Fresh Drops</span>
            </div>
            <h2 className="text-2xl sm:text-3xl font-black text-gray-900 tracking-tight">
              New Arrivals
            </h2>
          </div>
          <Link
            to="/products?sortBy=newest"
            className="text-sm font-bold text-emerald-600 hover:text-emerald-700 flex items-center gap-1 group"
          >
            <span>View All New</span>
            <ChevronRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
          </Link>
        </div>

        <ProductGrid products={newArrivals.slice(0, 8)} loading={loading} />
      </section>
    </div>
  );
};

export default Home;
