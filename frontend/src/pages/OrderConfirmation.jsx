import React, { useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  CheckCircle2, Package, ArrowRight, Truck,
  Home, ShoppingBag, Sparkles
} from 'lucide-react';

const OrderConfirmation = () => {
  const location = useLocation();
  const { orderId, total, orderData } = location.state || {};

  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const orderNum = orderId || `ORD-${Date.now()}`;
  const orderTotal = total || orderData?.total || 0;

  const steps = [
    { label: 'Order Placed', done: true, icon: CheckCircle2 },
    { label: 'Processing', done: false, icon: Package },
    { label: 'Shipped', done: false, icon: Truck },
    { label: 'Delivered', done: false, icon: Home },
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-emerald-50 via-white to-teal-50 flex items-center justify-center py-16 px-4">
      <div className="max-w-lg w-full">

        {/* Success animation card */}
        <div className="bg-white rounded-3xl shadow-2xl shadow-emerald-100 border border-emerald-100 overflow-hidden">

          {/* Green header */}
          <div className="bg-gradient-to-br from-emerald-500 to-teal-500 px-8 py-10 text-center relative overflow-hidden">
            {/* Decorative circles */}
            <div className="absolute top-0 right-0 w-48 h-48 bg-white/10 rounded-full -translate-y-1/2 translate-x-1/2" />
            <div className="absolute bottom-0 left-0 w-32 h-32 bg-white/10 rounded-full translate-y-1/2 -translate-x-1/2" />

            <div className="relative">
              <div className="w-20 h-20 bg-white/20 rounded-full flex items-center justify-center mx-auto mb-4 ring-4 ring-white/30">
                <CheckCircle2 className="w-10 h-10 text-white" />
              </div>
              <h1 className="text-3xl font-black text-white mb-2">Order Placed! 🎉</h1>
              <p className="text-emerald-100 text-sm font-medium">
                Thank you for shopping with Cartly
              </p>
            </div>
          </div>

          {/* Content */}
          <div className="px-8 py-6 space-y-6">
            {/* Order ID */}
            <div className="bg-gray-50 rounded-2xl p-4 text-center">
              <p className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-1">Order Number</p>
              <p className="text-xl font-black text-gray-900 font-mono tracking-wide">#{orderNum}</p>
              {orderTotal > 0 && (
                <p className="text-sm text-gray-500 mt-1">
                  Total: <span className="font-black text-emerald-700">${orderTotal.toFixed(2)}</span>
                </p>
              )}
            </div>

            {/* What happens next */}
            <div>
              <p className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3 flex items-center gap-2">
                <Sparkles className="w-3.5 h-3.5 text-amber-500" /> What happens next
              </p>
              <div className="space-y-3">
                {steps.map((step, idx) => {
                  const Icon = step.icon;
                  const isPast = idx === 0;
                  const isCurrent = idx === 1;
                  return (
                    <div key={step.label} className="flex items-center gap-3">
                      <div className={`w-8 h-8 rounded-xl flex items-center justify-center shrink-0 ${
                        isPast ? 'bg-emerald-500' :
                        isCurrent ? 'bg-emerald-100' : 'bg-gray-100'
                      }`}>
                        <Icon className={`w-4 h-4 ${
                          isPast ? 'text-white' :
                          isCurrent ? 'text-emerald-600' : 'text-gray-400'
                        }`} />
                      </div>
                      <div className="flex-1">
                        <p className={`text-sm font-bold ${
                          isPast ? 'text-emerald-700' :
                          isCurrent ? 'text-gray-900' : 'text-gray-400'
                        }`}>{step.label}</p>
                        {isPast && <p className="text-xs text-emerald-600">Completed ✓</p>}
                        {isCurrent && <p className="text-xs text-gray-400">We're preparing your order</p>}
                      </div>
                      {idx < steps.length - 1 && (
                        <div className="w-px h-4 bg-gray-200 ml-4 absolute translate-x-3.5 translate-y-6" />
                      )}
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Email note */}
            <div className="bg-blue-50 border border-blue-100 rounded-2xl p-4">
              <p className="text-sm text-blue-800">
                📧 A confirmation email has been sent with your order details and tracking information.
              </p>
            </div>

            {/* CTA Buttons */}
            <div className="space-y-3">
              <Link to="/orders"
                className="w-full flex items-center justify-center gap-2 py-4 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold rounded-2xl transition-all shadow-lg shadow-emerald-600/25 active:scale-95">
                <Package className="w-4 h-4" /> Track My Order
              </Link>
              <Link to="/products"
                className="w-full flex items-center justify-center gap-2 py-3.5 border-2 border-gray-200 text-gray-700 font-bold rounded-2xl hover:bg-gray-50 transition-all">
                <ShoppingBag className="w-4 h-4" /> Continue Shopping
                <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
          </div>
        </div>

        {/* Satisfaction badge */}
        <div className="text-center mt-6 text-sm text-gray-500">
          <span className="font-semibold">⭐ 100% Satisfaction Guaranteed</span>
          {' · '}
          <span>Easy 30-day returns</span>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmation;
