import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Filter, SlidersHorizontal, Search, RotateCcw, ChevronLeft, ChevronRight, Star } from 'lucide-react';
import ProductGrid from '../components/product/ProductGrid';
import { productService } from '../services/productService';
import { categoryService } from '../services/categoryService';

const Products = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Filters state
  const keywordParam = searchParams.get('keyword') || '';
  const categoryParam = searchParams.get('category') || 'all';
  const sortByParam = searchParams.get('sortBy') || 'newest';

  const [keyword, setKeyword] = useState(keywordParam);
  const [selectedCategory, setSelectedCategory] = useState(categoryParam);
  const [maxPrice, setMaxPrice] = useState(3500);
  const [minRating, setMinRating] = useState(0);
  const [sortBy, setSortBy] = useState(sortByParam);
  const [mobileFilterOpen, setMobileFilterOpen] = useState(false);

  // Synchronize state with URL search params
  useEffect(() => {
    setKeyword(searchParams.get('keyword') || '');
    setSelectedCategory(searchParams.get('category') || 'all');
    setSortBy(searchParams.get('sortBy') || 'newest');
    setCurrentPage(0);
  }, [searchParams]);

  useEffect(() => {
    categoryService
      .getCategories(true)
      .then((res) => {
        if (res.data) setCategories(res.data);
      })
      .catch(() => {});
  }, []);

  useEffect(() => {
    const fetchCatalog = async () => {
      try {
        setLoading(true);
        const res = await productService.getProducts({
          keyword: keyword || undefined,
          category: selectedCategory !== 'all' ? selectedCategory : undefined,
          maxPrice: maxPrice < 3500 ? maxPrice : undefined,
          minRating: minRating > 0 ? minRating : undefined,
          sortBy,
          page: currentPage,
          size: 12,
        });

        if (res.data) {
          setProducts(res.data.content || []);
          setTotalPages(res.data.totalPages || 1);
          setTotalElements(res.data.totalElements || 0);
        }
      } catch (err) {
        console.error('Error fetching catalog:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchCatalog();
  }, [keyword, selectedCategory, maxPrice, minRating, sortBy, currentPage]);

  const handleResetFilters = () => {
    setKeyword('');
    setSelectedCategory('all');
    setMaxPrice(3500);
    setMinRating(0);
    setSortBy('newest');
    setCurrentPage(0);
    setSearchParams({});
  };

  const handleCategorySelect = (catName) => {
    setSelectedCategory(catName);
    setCurrentPage(0);
    const newParams = new URLSearchParams(searchParams);
    if (catName === 'all') {
      newParams.delete('category');
    } else {
      newParams.set('category', catName);
    }
    setSearchParams(newParams);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header bar */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-6 border-b border-gray-200">
        <div>
          <h1 className="text-3xl font-black text-gray-900 tracking-tight">Products Catalog</h1>
          <p className="text-sm text-gray-500 mt-1">
            Showing <span className="font-bold text-gray-900">{totalElements}</span> results
            {selectedCategory !== 'all' && <span> in <strong className="text-emerald-600">{selectedCategory}</strong></span>}
            {keyword && <span> for "<strong>{keyword}</strong>"</span>}
          </p>
        </div>

        <div className="flex items-center gap-3">
          {/* Mobile Filter Trigger */}
          <button
            onClick={() => setMobileFilterOpen(!mobileFilterOpen)}
            className="lg:hidden flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-xl text-sm font-semibold text-gray-700 shadow-sm"
          >
            <Filter className="w-4 h-4 text-emerald-600" />
            <span>Filters</span>
          </button>

          {/* Sort Selector */}
          <div className="flex items-center gap-2">
            <span className="text-xs font-bold text-gray-500 uppercase tracking-wider hidden sm:inline">Sort:</span>
            <select
              value={sortBy}
              onChange={(e) => {
                setSortBy(e.target.value);
                const newParams = new URLSearchParams(searchParams);
                newParams.set('sortBy', e.target.value);
                setSearchParams(newParams);
              }}
              className="px-3.5 py-2 bg-white border border-gray-200 rounded-xl text-sm font-semibold text-gray-800 focus:outline-none focus:ring-2 focus:ring-emerald-500 shadow-sm"
            >
              <option value="newest">Newest Arrivals</option>
              <option value="price_asc">Price: Low to High</option>
              <option value="price_desc">Price: High to Low</option>
              <option value="rating">Highest Rated</option>
            </select>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-8 items-start">
        {/* Sidebar Filters */}
        <aside
          className={`lg:block ${
            mobileFilterOpen ? 'block' : 'hidden'
          } bg-white p-6 rounded-2xl border border-gray-200/80 shadow-sm space-y-6`}
        >
          <div className="flex items-center justify-between pb-4 border-b border-gray-100">
            <div className="flex items-center gap-2 font-bold text-gray-900 text-base">
              <SlidersHorizontal className="w-4 h-4 text-emerald-600" />
              <span>Refine Search</span>
            </div>
            <button
              onClick={handleResetFilters}
              className="flex items-center gap-1 text-xs font-semibold text-gray-400 hover:text-emerald-600 transition-colors"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              <span>Reset</span>
            </button>
          </div>

          {/* Search Box */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-gray-500 mb-2">
              Keyword
            </label>
            <div className="relative">
              <input
                type="text"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                placeholder="Search catalog..."
                className="w-full pl-9 pr-3 py-2 text-sm bg-gray-50 border border-gray-200 rounded-xl focus:bg-white focus:outline-none focus:ring-2 focus:ring-emerald-500"
              />
              <Search className="w-4 h-4 text-gray-400 absolute left-3 top-1/2 -translate-y-1/2" />
            </div>
          </div>

          {/* Categories */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-gray-500 mb-2">
              Category
            </label>
            <div className="space-y-1">
              <button
                onClick={() => handleCategorySelect('all')}
                className={`w-full text-left px-3 py-2 rounded-xl text-sm font-semibold transition-all ${
                  selectedCategory === 'all'
                    ? 'bg-emerald-50 text-emerald-700 font-bold'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                }`}
              >
                All Departments
              </button>
              {categories.map((cat) => (
                <button
                  key={cat.id}
                  onClick={() => handleCategorySelect(cat.name)}
                  className={`w-full text-left px-3 py-2 rounded-xl text-sm font-semibold transition-all ${
                    selectedCategory.toLowerCase() === cat.name.toLowerCase()
                      ? 'bg-emerald-50 text-emerald-700 font-bold'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                >
                  {cat.name}
                </button>
              ))}
            </div>
          </div>

          {/* Price Range */}
          <div>
            <div className="flex items-center justify-between mb-2">
              <label className="text-xs font-bold uppercase tracking-wider text-gray-500">
                Max Price
              </label>
              <span className="text-sm font-black text-emerald-600">${maxPrice}</span>
            </div>
            <input
              type="range"
              min="50"
              max="3500"
              step="50"
              value={maxPrice}
              onChange={(e) => setMaxPrice(Number(e.target.value))}
              className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-emerald-600"
            />
            <div className="flex justify-between text-[11px] text-gray-400 mt-1">
              <span>$50</span>
              <span>$1750</span>
              <span>$3500+</span>
            </div>
          </div>

          {/* Minimum Rating */}
          <div>
            <label className="block text-xs font-bold uppercase tracking-wider text-gray-500 mb-2">
              Minimum Rating
            </label>
            <div className="space-y-1.5">
              {[4.5, 4.0, 3.5].map((ratingVal) => (
                <button
                  key={ratingVal}
                  onClick={() => setMinRating(minRating === ratingVal ? 0 : ratingVal)}
                  className={`w-full flex items-center justify-between px-3 py-2 rounded-xl text-sm font-semibold border transition-all ${
                    minRating === ratingVal
                      ? 'bg-emerald-50 border-emerald-300 text-emerald-800'
                      : 'border-gray-100 hover:bg-gray-50 text-gray-600'
                  }`}
                >
                  <div className="flex items-center gap-1.5">
                    <Star className="w-4 h-4 fill-amber-400 text-amber-400" />
                    <span>{ratingVal} Stars & above</span>
                  </div>
                  {minRating === ratingVal && (
                    <span className="text-xs font-bold text-emerald-600">Active</span>
                  )}
                </button>
              ))}
            </div>
          </div>
        </aside>

        {/* Product Grid & Pagination */}
        <div className="lg:col-span-3 space-y-8">
          <ProductGrid products={products} loading={loading} />

          {/* Pagination Controls */}
          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-2 pt-6 border-t border-gray-200">
              <button
                disabled={currentPage === 0}
                onClick={() => setCurrentPage((prev) => Math.max(0, prev - 1))}
                className="flex items-center gap-1 px-4 py-2 rounded-xl border border-gray-200 text-sm font-semibold text-gray-700 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed shadow-sm transition-all"
              >
                <ChevronLeft className="w-4 h-4" />
                <span>Previous</span>
              </button>

              <div className="flex items-center gap-1">
                {[...Array(totalPages)].map((_, i) => (
                  <button
                    key={i}
                    onClick={() => setCurrentPage(i)}
                    className={`w-10 h-10 rounded-xl text-sm font-bold transition-all ${
                      currentPage === i
                        ? 'bg-emerald-600 text-white shadow-md'
                        : 'text-gray-700 hover:bg-gray-100'
                    }`}
                  >
                    {i + 1}
                  </button>
                ))}
              </div>

              <button
                disabled={currentPage >= totalPages - 1}
                onClick={() => setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1))}
                className="flex items-center gap-1 px-4 py-2 rounded-xl border border-gray-200 text-sm font-semibold text-gray-700 hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed shadow-sm transition-all"
              >
                <span>Next</span>
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Products;
