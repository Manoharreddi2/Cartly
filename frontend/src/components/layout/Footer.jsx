import React from 'react';
import { Link } from 'react-router-dom';
import { ShoppingCart, ShieldCheck, Truck, RotateCcw, Headphones, Mail } from 'lucide-react';

const Footer = () => {
  return (
    <footer className="bg-slate-950 text-gray-300 pt-16 pb-8 border-t border-slate-800">
      {/* Value props banner */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-12 mb-12 border-b border-slate-800/80">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-8">
          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center shrink-0">
              <Truck className="w-6 h-6" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-white">Free Fast Shipping</h4>
              <p className="text-xs text-gray-400">On all eligible orders over $100</p>
            </div>
          </div>

          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center shrink-0">
              <ShieldCheck className="w-6 h-6" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-white">100% Secure Payments</h4>
              <p className="text-xs text-gray-400">End-to-end encrypted checkout</p>
            </div>
          </div>

          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center shrink-0">
              <RotateCcw className="w-6 h-6" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-white">30-Day Easy Returns</h4>
              <p className="text-xs text-gray-400">Hassle-free money-back guarantee</p>
            </div>
          </div>

          <div className="flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-emerald-500/10 text-emerald-400 flex items-center justify-center shrink-0">
              <Headphones className="w-6 h-6" />
            </div>
            <div>
              <h4 className="text-sm font-bold text-white">24/7 Dedicated Support</h4>
              <p className="text-xs text-gray-400">Expert help whenever you need</p>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-10 pb-12">
          {/* Brand info */}
          <div className="lg:col-span-2">
            <div className="flex items-center gap-2.5 mb-4">
              <div className="w-10 h-10 rounded-xl bg-emerald-600 flex items-center justify-center text-white shadow-md">
                <ShoppingCart className="w-5 h-5" />
              </div>
              <span className="text-2xl font-black tracking-tight text-white">
                Cart<span className="text-emerald-500">ly</span>
              </span>
            </div>
            <p className="text-sm text-gray-400 mb-6 max-w-sm">
              Everything You Need, Just a Cart Away. Delivering premium electronics, trending fashion, high-tech laptops, and lifestyle essentials to your doorstep.
            </p>
            <div className="flex items-center gap-3 text-xs text-gray-400">
              <Mail className="w-4 h-4 text-emerald-500" />
              <span>support@cartly.com | +1 (800) CARTLY-01</span>
            </div>
          </div>

          {/* Shop */}
          <div>
            <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4">Shop</h3>
            <ul className="space-y-2.5 text-sm">
              <li><Link to="/products" className="hover:text-emerald-400 transition-colors">All Products</Link></li>
              <li><Link to="/products?category=Electronics" className="hover:text-emerald-400 transition-colors">Electronics</Link></li>
              <li><Link to="/products?category=Mobiles" className="hover:text-emerald-400 transition-colors">Smartphones</Link></li>
              <li><Link to="/products?category=Laptops" className="hover:text-emerald-400 transition-colors">Laptops</Link></li>
              <li><Link to="/products?category=Fashion" className="hover:text-emerald-400 transition-colors">Fashion</Link></li>
            </ul>
          </div>

          {/* Account */}
          <div>
            <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4">Account</h3>
            <ul className="space-y-2.5 text-sm">
              <li><Link to="/profile" className="hover:text-emerald-400 transition-colors">My Profile</Link></li>
              <li><Link to="/orders" className="hover:text-emerald-400 transition-colors">Track Orders</Link></li>
              <li><Link to="/wishlist" className="hover:text-emerald-400 transition-colors">Wishlist</Link></li>
              <li><Link to="/cart" className="hover:text-emerald-400 transition-colors">Shopping Cart</Link></li>
              <li><Link to="/login" className="hover:text-emerald-400 transition-colors">Sign In</Link></li>
            </ul>
          </div>

          {/* Microservices info */}
          <div>
            <h3 className="text-sm font-bold text-white uppercase tracking-wider mb-4">Platform Architecture</h3>
            <ul className="space-y-2 text-xs text-gray-400">
              <li>Spring Cloud Gateway (8080)</li>
              <li>Auth Service & JWT (8081)</li>
              <li>Product & Inventory (8083/88)</li>
              <li>Cart & Orders (8085/86)</li>
              <li>Payment & Notifications (8087/89)</li>
              <li>MySQL Relational Storage</li>
            </ul>
          </div>
        </div>

        {/* Bottom copyright */}
        <div className="pt-8 border-t border-slate-800 text-xs text-gray-500 flex flex-col sm:flex-row items-center justify-between gap-4">
          <p>© {new Date().getFullYear()} Cartly E-Commerce Platform. All rights reserved.</p>
          <div className="flex items-center gap-4">
            <span className="px-2 py-1 bg-slate-900 rounded border border-slate-800 text-gray-400">VISA</span>
            <span className="px-2 py-1 bg-slate-900 rounded border border-slate-800 text-gray-400">MasterCard</span>
            <span className="px-2 py-1 bg-slate-900 rounded border border-slate-800 text-gray-400">Apple Pay</span>
            <span className="px-2 py-1 bg-slate-900 rounded border border-slate-800 text-gray-400">UPI</span>
            <span className="px-2 py-1 bg-slate-900 rounded border border-slate-800 text-gray-400">COD</span>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
