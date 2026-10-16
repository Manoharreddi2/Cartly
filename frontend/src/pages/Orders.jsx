import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Package,
  Clock,
  CheckCircle2,
  Truck,
  XCircle,
  AlertCircle,
  ChevronDown,
  ChevronRight,
  RotateCcw,
  ShoppingBag,
  Search,
  Filter,
  Calendar,
  DollarSign,
  ArrowRight,
} from 'lucide-react';
import { orderService } from '../services/orderService';
import { useAuth } from '../context/AuthContext';

// ─── Status config ────────────────────────────────────────────────────────────
const STATUS_CONFIG = {
  PENDING: {
    label: 'Pending',
    icon: Clock,
    color: 'text-amber-700',
    bg: 'bg-amber-50',
    border: 'border-amber-200',
    dot: 'bg-amber-400',
    step: 0,
  },
  CONFIRMED: {
    label: 'Confirmed',
    icon: CheckCircle2,
    color: 'text-sky-700',
    bg: 'bg-sky-50',
    border: 'border-sky-200',
    dot: 'bg-sky-400',
    step: 1,
  },
  PROCESSING: {
    label: 'Processing',
    icon: Package,
    color: 'text-violet-700',
    bg: 'bg-violet-50',
    border: 'border-violet-200',
    dot: 'bg-violet-400',
    step: 1,
  },
  SHIPPED: {
    label: 'Shipped',
    icon: Truck,
    color: 'text-indigo-700',
    bg: 'bg-indigo-50',
    border: 'border-indigo-200',
    dot: 'bg-indigo-400',
    step: 2,
  },
  DELIVERED: {
    label: 'Delivered',
    icon: CheckCircle2,
    color: 'text-emerald-700',
    bg: 'bg-emerald-50',
    border: 'border-emerald-200',
    dot: 'bg-emerald-500',
    step: 3,
  },
  CANCELLED: {
    label: 'Cancelled',
    icon: XCircle,
    color: 'text-rose-700',
    bg: 'bg-rose-50',
    border: 'border-rose-200',
    dot: 'bg-rose-400',
    step: -1,
  },
};

const PROGRESS_STEPS = ['Confirmed', 'Processing / Packed', 'Shipped', 'Delivered'];

// ─── Status Badge ─────────────────────────────────────────────────────────────
const StatusBadge = ({ status }) => {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.PENDING;
  const Icon = cfg.icon;
  return (
    <span
      className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-bold border ${cfg.color} ${cfg.bg} ${cfg.border}`}
    >
      <span className={`w-1.5 h-1.5 rounded-full ${cfg.dot}`} />
      <Icon className="w-3.5 h-3.5" />
      {cfg.label}
    </span>
  );
};

// ─── Progress Tracker ─────────────────────────────────────────────────────────
const OrderProgress = ({ status }) => {
  const cfg = STATUS_CONFIG[status] || STATUS_CONFIG.PENDING;
  if (cfg.step === -1) return null; // don't show for cancelled

  return (
    <div className="mt-4 px-1">
      <div className="flex items-center gap-0">
        {PROGRESS_STEPS.map((step, idx) => {
          const done = idx <= cfg.step;
          const active = idx === cfg.step;
          return (
            <React.Fragment key={step}>
              <div className="flex flex-col items-center gap-1 min-w-0">
                <div
                  className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold border-2 transition-all ${
                    done
                      ? 'bg-emerald-500 border-emerald-500 text-white shadow-sm shadow-emerald-300'
                      : 'bg-white border-gray-200 text-gray-400'
                  } ${active ? 'ring-4 ring-emerald-100' : ''}`}
                >
                  {done ? <CheckCircle2 className="w-4 h-4" /> : <span>{idx + 1}</span>}
                </div>
                <span
                  className={`text-[10px] font-semibold text-center leading-tight max-w-[60px] hidden sm:block ${
                    done ? 'text-emerald-700' : 'text-gray-400'
                  }`}
                >
                  {step}
                </span>
              </div>
              {idx < PROGRESS_STEPS.length - 1 && (
                <div
                  className={`flex-1 h-0.5 mx-1 rounded-full transition-all ${
                    idx < cfg.step ? 'bg-emerald-400' : 'bg-gray-200'
                  }`}
                />
              )}
            </React.Fragment>
          );
        })}
      </div>
    </div>
  );
};

// ─── Single Order Card ────────────────────────────────────────────────────────
const OrderCard = ({ order, onCancel }) => {
  const [expanded, setExpanded] = useState(false);
  const cfg = STATUS_CONFIG[order.status] || STATUS_CONFIG.PENDING;
  const createdAt = order.createdAt
    ? new Date(order.createdAt).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
      })
    : '—';

  const canCancel = ['PENDING', 'CONFIRMED'].includes(order.status);
  const itemCount = order.items?.length || 0;
  const previewItems = order.items?.slice(0, 3) || [];

  return (
    <div className="bg-white rounded-2xl border border-gray-200/80 shadow-sm hover:shadow-md hover:border-gray-300 transition-all overflow-hidden">
      {/* Card Header */}
      <div className="p-5 sm:p-6">
        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-3">
          {/* Order info left */}
          <div className="flex items-start gap-4">
            <div
              className={`w-11 h-11 rounded-xl flex items-center justify-center shrink-0 ${cfg.bg} ${cfg.color}`}
            >
              <Package className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2 flex-wrap">
                <span className="font-black text-gray-900 text-base">
                  #{order.orderNumber || order.id}
                </span>
                <StatusBadge status={order.status} />
              </div>
              <div className="flex items-center gap-3 mt-1 text-xs text-gray-500 flex-wrap">
                <span className="flex items-center gap-1">
                  <Calendar className="w-3.5 h-3.5" />
                  {createdAt}
                </span>
                <span className="flex items-center gap-1">
                  <ShoppingBag className="w-3.5 h-3.5" />
                  {itemCount} item{itemCount !== 1 ? 's' : ''}
                </span>
              </div>
            </div>
          </div>

          {/* Total & actions right */}
          <div className="flex items-center gap-3 sm:flex-col sm:items-end">
            <div className="text-right">
              <p className="text-2xl font-black text-gray-900">${(order.total || order.totalAmount)?.toFixed(2) ?? '—'}</p>
              <p className="text-xs text-gray-400">Order Total</p>
            </div>
          </div>
        </div>

        {/* Item thumbnails preview */}
        {previewItems.length > 0 && (
          <div className="mt-4 flex items-center gap-2">
            {previewItems.map((item, idx) => (
              <div
                key={idx}
                className="w-12 h-12 rounded-xl border border-gray-100 bg-gray-50 overflow-hidden shrink-0"
              >
                <img
                  src={
                    item.productImage ||
                    'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100'
                  }
                  alt={item.productName}
                  className="w-full h-full object-cover"
                />
              </div>
            ))}
            {itemCount > 3 && (
              <div className="w-12 h-12 rounded-xl border border-gray-100 bg-gray-100 flex items-center justify-center text-xs font-bold text-gray-500">
                +{itemCount - 3}
              </div>
            )}
            <div className="flex-1 min-w-0">
              <p className="text-xs text-gray-600 font-medium truncate">
                {previewItems[0]?.productName}
                {itemCount > 1 ? ` & ${itemCount - 1} more` : ''}
              </p>
            </div>
          </div>
        )}

        {/* Progress tracker */}
        {order.status !== 'CANCELLED' && <OrderProgress status={order.status} />}

        {/* Actions Row */}
        <div className="mt-4 pt-4 border-t border-gray-100 flex items-center justify-between flex-wrap gap-2">
          <button
            onClick={() => setExpanded(!expanded)}
            className="inline-flex items-center gap-1.5 text-sm font-semibold text-emerald-700 hover:text-emerald-800 transition-colors"
          >
            {expanded ? 'Hide Details' : 'View Details'}
            <ChevronDown
              className={`w-4 h-4 transition-transform duration-200 ${expanded ? 'rotate-180' : ''}`}
            />
          </button>

          <div className="flex items-center gap-2">
            {canCancel && (
              <button
                onClick={() => onCancel(order.id)}
                className="inline-flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-rose-600 bg-rose-50 hover:bg-rose-100 border border-rose-200 rounded-xl transition-all"
              >
                <XCircle className="w-3.5 h-3.5" />
                Cancel Order
              </button>
            )}
            <Link
              to="/products"
              className="inline-flex items-center gap-1.5 px-3.5 py-1.5 text-xs font-bold text-emerald-700 bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 rounded-xl transition-all"
            >
              <RotateCcw className="w-3.5 h-3.5" />
              Reorder
            </Link>
          </div>
        </div>
      </div>

      {/* Expanded Details */}
      {expanded && (
        <div className="border-t border-gray-100 bg-gray-50/50 px-5 sm:px-6 py-5 space-y-4 animate-in fade-in slide-in-from-top-1 duration-200">
          {/* Order Items Table */}
          <div>
            <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">
              Order Items
            </h4>
            <div className="space-y-3">
              {order.items?.map((item, idx) => (
                <div
                  key={idx}
                  className="flex items-center gap-3 bg-white rounded-xl p-3 border border-gray-100 shadow-sm"
                >
                  <div className="w-14 h-14 rounded-lg overflow-hidden border border-gray-100 bg-gray-50 shrink-0">
                    <img
                      src={
                        item.productImage ||
                        'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=100'
                      }
                      alt={item.productName}
                      className="w-full h-full object-cover"
                    />
                  </div>
                  <div className="flex-1 min-w-0">
                    <Link
                      to={`/products/${item.productId}`}
                      className="text-sm font-bold text-gray-900 hover:text-emerald-600 transition-colors line-clamp-1"
                    >
                      {item.productName}
                    </Link>
                    <p className="text-xs text-gray-500 mt-0.5">
                      Qty: {item.quantity} × ${(item.price || item.unitPrice)?.toFixed(2)}
                    </p>
                  </div>
                  <div className="shrink-0 text-right">
                    <p className="text-sm font-black text-gray-900">
                      ${(item.itemTotal || (item.quantity * (item.price || item.unitPrice)))?.toFixed(2)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Order Summary */}
          <div className="bg-white rounded-xl border border-gray-100 p-4 space-y-2.5 text-sm">
            <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-3">
              Price Breakdown
            </h4>
            <div className="flex justify-between text-gray-600">
              <span>Subtotal</span>
              <span className="font-semibold text-gray-900">
                ${(order.subtotal || order.totalAmount)?.toFixed(2) ?? '—'}
              </span>
            </div>
            {order.discount > 0 && (
              <div className="flex justify-between text-emerald-600">
                <span>Discount</span>
                <span className="font-bold">-${order.discount?.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-gray-600">
              <span>Delivery</span>
              <span className="font-semibold">
                {!order.deliveryFee || order.deliveryFee === 0 ? (
                  <span className="text-emerald-600 font-bold">FREE</span>
                ) : (
                  `$${order.deliveryFee?.toFixed(2)}`
                )}
              </span>
            </div>
            {order.tax > 0 && (
              <div className="flex justify-between text-gray-600">
                <span>Tax</span>
                <span className="font-semibold">${order.tax?.toFixed(2)}</span>
              </div>
            )}
            <div className="pt-2.5 border-t border-gray-100 flex justify-between items-baseline">
              <span className="font-extrabold text-gray-900">Total</span>
              <span className="text-xl font-black text-emerald-700">
                ${(order.total || order.totalAmount)?.toFixed(2) ?? '—'}
              </span>
            </div>
          </div>

          {/* Shipping Info */}
          {(order.shippingAddress || order.streetAddress) && (
            <div className="bg-white rounded-xl border border-gray-100 p-4 text-sm">
              <h4 className="text-xs font-bold text-gray-500 uppercase tracking-wider mb-2">
                Shipping Address
              </h4>
              <p className="text-gray-700 leading-relaxed">
                {typeof order.shippingAddress === 'string'
                  ? order.shippingAddress
                  : [
                      order.shippingAddress?.street || order.streetAddress,
                      order.shippingAddress?.city || order.city,
                      order.shippingAddress?.state || order.state,
                      order.shippingAddress?.zipCode || order.zipCode,
                      order.shippingAddress?.country || order.country,
                    ]
                      .filter(Boolean)
                      .join(', ')}
              </p>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

// ─── Skeleton Loader ──────────────────────────────────────────────────────────
const OrderSkeleton = () => (
  <div className="bg-white rounded-2xl border border-gray-200 p-5 sm:p-6 animate-pulse space-y-4">
    <div className="flex items-start gap-4">
      <div className="w-11 h-11 rounded-xl bg-gray-200 shrink-0" />
      <div className="flex-1 space-y-2">
        <div className="h-5 bg-gray-200 rounded w-1/3" />
        <div className="h-3 bg-gray-200 rounded w-1/4" />
      </div>
      <div className="h-8 bg-gray-200 rounded w-20" />
    </div>
    <div className="flex gap-2">
      {[...Array(3)].map((_, i) => (
        <div key={i} className="w-12 h-12 bg-gray-200 rounded-xl" />
      ))}
    </div>
  </div>
);

// ─── Empty State ──────────────────────────────────────────────────────────────
const EmptyOrders = ({ filter }) => (
  <div className="text-center py-20">
    <div className="w-20 h-20 bg-emerald-50 text-emerald-500 rounded-3xl flex items-center justify-center mx-auto mb-5 shadow-sm">
      <Package className="w-10 h-10" />
    </div>
    <h2 className="text-2xl font-black text-gray-900 tracking-tight mb-2">
      {filter !== 'ALL' ? `No ${filter.toLowerCase()} orders` : 'No Orders Yet'}
    </h2>
    <p className="text-sm text-gray-500 max-w-sm mx-auto mb-8">
      {filter !== 'ALL'
        ? 'Try changing the filter to see other orders.'
        : "You haven't placed any orders yet. Start shopping and your orders will appear here."}
    </p>
    <Link
      to="/products"
      className="inline-flex items-center gap-2 px-8 py-3.5 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-sm rounded-2xl shadow-lg shadow-emerald-600/25 active:scale-95 transition-all"
    >
      <span>Start Shopping</span>
      <ArrowRight className="w-4 h-4" />
    </Link>
  </div>
);

// ─── Main Orders Page ─────────────────────────────────────────────────────────
const Orders = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [cancellingId, setCancellingId] = useState(null);

  const STATUS_FILTERS = ['ALL', 'PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        setError(null);
        const res = await orderService.getOrders();
        // Handle both array responses and wrapped responses
        const data = Array.isArray(res) ? res : res?.data ?? res?.content ?? [];
        setOrders(data);
      } catch (err) {
        setError(err.message || 'Failed to load orders');
      } finally {
        setLoading(false);
      }
    };
    fetchOrders();
  }, []);

  const handleCancel = async (orderId) => {
    if (!window.confirm('Are you sure you want to cancel this order?')) return;
    try {
      setCancellingId(orderId);
      await orderService.cancelOrder(orderId);
      setOrders((prev) =>
        prev.map((o) => (o.id === orderId ? { ...o, status: 'CANCELLED' } : o))
      );
    } catch (err) {
      alert(err.message || 'Failed to cancel order');
    } finally {
      setCancellingId(null);
    }
  };

  // Filter + search
  const filtered = orders.filter((o) => {
    const matchesFilter = filter === 'ALL' || o.status === filter;
    const q = searchQuery.toLowerCase().trim();
    const matchesSearch =
      !q ||
      String(o.orderNumber || o.id).toLowerCase().includes(q) ||
      o.items?.some((item) => item.productName?.toLowerCase().includes(q));
    return matchesFilter && matchesSearch;
  });

  // Stats
  const stats = {
    total: orders.length,
    delivered: orders.filter((o) => o.status === 'DELIVERED').length,
    active: orders.filter((o) => ['PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED'].includes(o.status)).length,
    totalSpent: orders
      .filter((o) => o.status !== 'CANCELLED')
      .reduce((sum, o) => sum + (o.total || o.totalAmount || 0), 0),
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-10 space-y-8">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-end sm:justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-xs font-bold text-emerald-600 uppercase tracking-wider mb-1">
            <Package className="w-4 h-4" />
            <span>Order History</span>
          </div>
          <h1 className="text-3xl sm:text-4xl font-black text-gray-900 tracking-tight">
            My Orders
          </h1>
          <p className="text-sm text-gray-500 mt-1">
            {user?.name && `Welcome back, ${user.name.split(' ')[0]}! `}
            Track and manage all your purchases.
          </p>
        </div>
        <Link
          to="/products"
          className="inline-flex items-center gap-2 px-5 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-sm rounded-xl shadow-sm active:scale-95 transition-all shrink-0"
        >
          <ShoppingBag className="w-4 h-4" />
          Shop More
        </Link>
      </div>

      {/* Stats Row */}
      {!loading && orders.length > 0 && (
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          {[
            { label: 'Total Orders', value: stats.total, icon: Package, color: 'text-slate-700', bg: 'bg-slate-50', border: 'border-slate-200' },
            { label: 'Active Orders', value: stats.active, icon: Truck, color: 'text-indigo-700', bg: 'bg-indigo-50', border: 'border-indigo-200' },
            { label: 'Delivered', value: stats.delivered, icon: CheckCircle2, color: 'text-emerald-700', bg: 'bg-emerald-50', border: 'border-emerald-200' },
            { label: 'Total Spent', value: `$${stats.totalSpent.toFixed(2)}`, icon: DollarSign, color: 'text-amber-700', bg: 'bg-amber-50', border: 'border-amber-200' },
          ].map(({ label, value, icon: Icon, color, bg, border }) => (
            <div
              key={label}
              className={`rounded-2xl border p-4 flex flex-col gap-2 ${bg} ${border}`}
            >
              <div className={`w-8 h-8 rounded-lg ${bg} flex items-center justify-center`}>
                <Icon className={`w-4 h-4 ${color}`} />
              </div>
              <p className={`text-xl font-black ${color}`}>{value}</p>
              <p className="text-xs text-gray-500 font-medium">{label}</p>
            </div>
          ))}
        </div>
      )}

      {/* Search + Filter Bar */}
      <div className="flex flex-col sm:flex-row gap-3">
        {/* Search */}
        <div className="relative flex-1">
          <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search by order # or product name..."
            className="w-full pl-10 pr-4 py-2.5 text-sm bg-white border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 transition-all"
          />
        </div>

        {/* Filter Dropdown */}
        <div className="relative">
          <Filter className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
          <select
            value={filter}
            onChange={(e) => setFilter(e.target.value)}
            className="pl-9 pr-8 py-2.5 text-sm bg-white border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 transition-all appearance-none cursor-pointer font-semibold text-gray-700"
          >
            {STATUS_FILTERS.map((s) => (
              <option key={s} value={s}>
                {s === 'ALL' ? 'All Orders' : STATUS_CONFIG[s]?.label || s}
              </option>
            ))}
          </select>
          <ChevronDown className="absolute right-2.5 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400 pointer-events-none" />
        </div>
      </div>

      {/* Orders List */}
      {loading ? (
        <div className="space-y-4">
          {[...Array(3)].map((_, i) => <OrderSkeleton key={i} />)}
        </div>
      ) : error ? (
        <div className="flex flex-col items-center justify-center py-20 gap-4 text-center">
          <div className="w-16 h-16 bg-rose-50 rounded-2xl flex items-center justify-center">
            <AlertCircle className="w-8 h-8 text-rose-500" />
          </div>
          <div>
            <p className="font-bold text-gray-900 mb-1">Failed to Load Orders</p>
            <p className="text-sm text-gray-500">{error}</p>
          </div>
          <button
            onClick={() => window.location.reload()}
            className="px-5 py-2 bg-emerald-600 text-white text-sm font-bold rounded-xl hover:bg-emerald-700 transition-colors"
          >
            Try Again
          </button>
        </div>
      ) : filtered.length === 0 ? (
        <EmptyOrders filter={filter} />
      ) : (
        <div className="space-y-4">
          <p className="text-xs font-semibold text-gray-400 uppercase tracking-wider">
            Showing {filtered.length} of {orders.length} order{orders.length !== 1 ? 's' : ''}
          </p>
          {filtered.map((order) => (
            <OrderCard
              key={order.id}
              order={order}
              onCancel={handleCancel}
              loading={cancellingId === order.id}
            />
          ))}
        </div>
      )}
    </div>
  );
};

export default Orders;
