import React from 'react';
import { Link } from 'react-router-dom';
import { Heart, ShoppingCart } from 'lucide-react';
import { useWishlist } from '../context/WishlistContext';

const Wishlist = () => {
  const { wishlist, moveToCart, removeFromWishlist } = useWishlist();

  if (wishlist.length === 0) {
    return <div className="max-w-3xl mx-auto px-4 py-20 text-center"><Heart className="w-12 h-12 text-rose-400 mx-auto mb-4" /><h1 className="text-2xl font-black">Your wishlist is empty</h1><p className="text-gray-500 mt-2 mb-6">Save products you love to find them later.</p><Link to="/products" className="inline-flex px-5 py-3 rounded-xl bg-emerald-600 text-white font-bold">Explore products</Link></div>;
  }

  return <div className="max-w-6xl mx-auto px-4 py-10"><h1 className="text-3xl font-black text-gray-900 mb-7">My Wishlist</h1><div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-5">{wishlist.map((item) => <article key={item.productId} className="bg-white border border-gray-200 rounded-2xl overflow-hidden shadow-sm"><Link to={`/products/${item.productId}`}><img src={item.productImage} alt={item.productName} className="w-full h-48 object-cover bg-gray-100" /></Link><div className="p-4"><Link to={`/products/${item.productId}`} className="font-bold text-gray-900 hover:text-emerald-700">{item.productName}</Link><p className="font-black text-emerald-700 mt-2">${(item.discountedPrice ?? item.price)?.toFixed(2)}</p><div className="flex gap-2 mt-4"><button onClick={() => moveToCart(item.productId)} className="flex-1 inline-flex justify-center items-center gap-2 py-2.5 rounded-xl bg-emerald-600 text-white text-sm font-bold"><ShoppingCart className="w-4 h-4" /> Move to cart</button><button onClick={() => removeFromWishlist(item.productId)} className="px-3 rounded-xl border border-rose-200 text-rose-600 text-sm font-bold">Remove</button></div></div></article>)}</div></div>;
};

export default Wishlist;
