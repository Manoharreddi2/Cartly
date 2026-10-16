import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  MapPin, ShoppingBag, CreditCard, CheckCircle2,
  ChevronRight, ChevronLeft, Plus, Truck, Shield,
  Tag, AlertCircle, Loader2
} from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/userService';
import { orderService } from '../services/orderService';
import { useToast } from '../context/ToastContext';

// ── Step Indicator ──────────────────────────────────────────────────────────────
const steps = [
  { id: 1, label: 'Address', icon: MapPin },
  { id: 2, label: 'Review', icon: ShoppingBag },
  { id: 3, label: 'Payment', icon: CreditCard },
];

const StepIndicator = ({ currentStep }) => (
  <div className="flex items-center justify-center gap-0 mb-10">
    {steps.map((step, idx) => {
      const Icon = step.icon;
      const done = currentStep > step.id;
      const active = currentStep === step.id;
      return (
        <React.Fragment key={step.id}>
          <div className="flex flex-col items-center gap-1.5">
            <div className={`w-11 h-11 rounded-2xl flex items-center justify-center transition-all duration-300 ${
              done ? 'bg-emerald-500 shadow-lg shadow-emerald-500/30' :
              active ? 'bg-emerald-600 shadow-lg shadow-emerald-600/30 ring-4 ring-emerald-100' :
              'bg-gray-100 border-2 border-gray-200'
            }`}>
              {done ? (
                <CheckCircle2 className="w-5 h-5 text-white" />
              ) : (
                <Icon className={`w-5 h-5 ${active ? 'text-white' : 'text-gray-400'}`} />
              )}
            </div>
            <span className={`text-xs font-bold ${active ? 'text-emerald-700' : done ? 'text-emerald-600' : 'text-gray-400'}`}>
              {step.label}
            </span>
          </div>
          {idx < steps.length - 1 && (
            <div className={`w-16 sm:w-24 h-0.5 mx-1 mb-5 rounded-full transition-all duration-500 ${
              currentStep > step.id ? 'bg-emerald-400' : 'bg-gray-200'
            }`} />
          )}
        </React.Fragment>
      );
    })}
  </div>
);

// ── Step 1: Address ─────────────────────────────────────────────────────────────
const AddressStep = ({ selectedAddress, setSelectedAddress, onNext }) => {
  const [addresses, setAddresses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showNew, setShowNew] = useState(false);
  const [form, setForm] = useState({ fullName: '', phone: '', streetAddress: '', city: '', state: '', zipCode: '', country: 'United States' });
  const [saving, setSaving] = useState(false);
  const { addToast } = useToast();

  useEffect(() => {
    const fetchAddresses = async () => {
      try {
        const res = await userService.getAddresses();
        const list = Array.isArray(res.data) ? res.data : [];
        setAddresses(list);
        if (list.length > 0 && !selectedAddress) setSelectedAddress(list[0]);
      } catch {
        // Use mock address if API not available
        const mock = [{ id: 1, fullName: 'Demo Customer', phone: '555-0100', streetAddress: '123 Main St', city: 'New York', state: 'NY', zipCode: '10001', country: 'United States' }];
        setAddresses(mock);
        setSelectedAddress(mock[0]);
      } finally {
        setLoading(false);
      }
    };
    fetchAddresses();
  }, []);

  const handleSave = async () => {
    if (!form.fullName || !form.phone || !form.streetAddress || !form.city || !form.state || !form.zipCode) {
      addToast('Please fill all address fields', 'error'); return;
    }
    try {
      setSaving(true);
      const res = await userService.addAddress(form);
      const newAddr = res.data;
      setAddresses(prev => [...prev, newAddr]);
      setSelectedAddress(newAddr);
      setShowNew(false);
      setForm({ fullName: '', phone: '', streetAddress: '', city: '', state: '', zipCode: '', country: 'United States' });
      addToast('Address added!', 'success');
    } catch {
      // Simulate add
      const fakeAddr = { ...form, id: Date.now() };
      setAddresses(prev => [...prev, fakeAddr]);
      setSelectedAddress(fakeAddr);
      setShowNew(false);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return (
    <div className="flex justify-center py-16">
      <Loader2 className="w-8 h-8 text-emerald-500 animate-spin" />
    </div>
  );

  return (
    <div className="space-y-4">
      <h2 className="text-xl font-black text-gray-900">Select Delivery Address</h2>

      {addresses.map(addr => (
        <button
          key={addr.id}
          onClick={() => setSelectedAddress(addr)}
          className={`w-full text-left p-5 rounded-2xl border-2 transition-all ${
            selectedAddress?.id === addr.id
              ? 'border-emerald-500 bg-emerald-50/50 shadow-sm'
              : 'border-gray-200 bg-white hover:border-gray-300'
          }`}
        >
          <div className="flex items-start gap-3">
            <div className={`w-5 h-5 rounded-full border-2 mt-0.5 flex items-center justify-center shrink-0 ${
              selectedAddress?.id === addr.id ? 'border-emerald-500 bg-emerald-500' : 'border-gray-300'
            }`}>
              {selectedAddress?.id === addr.id && <div className="w-2 h-2 bg-white rounded-full" />}
            </div>
            <div>
              <p className="font-bold text-gray-900 text-sm">{addr.fullName}</p>
              <p className="text-sm text-gray-600">{addr.streetAddress}</p>
              <p className="text-sm text-gray-600">{addr.city}, {addr.state} {addr.zipCode}</p>
              <p className="text-sm text-gray-500">{addr.country}</p>
            </div>
          </div>
        </button>
      ))}

      {showNew ? (
        <div className="p-5 rounded-2xl border-2 border-emerald-200 bg-emerald-50/30 space-y-3">
          <h3 className="font-bold text-gray-800 text-sm">New Address</h3>
          <div className="grid grid-cols-2 gap-3">
            <input placeholder="Full Name" value={form.fullName} onChange={e => setForm({...form, fullName: e.target.value})}
              className="px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
            <input placeholder="Phone Number" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})}
              className="px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
          </div>
          <input placeholder="Street Address" value={form.streetAddress} onChange={e => setForm({...form, streetAddress: e.target.value})}
            className="w-full px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
          <div className="grid grid-cols-2 gap-3">
            <input placeholder="City" value={form.city} onChange={e => setForm({...form, city: e.target.value})}
              className="px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
            <input placeholder="State" value={form.state} onChange={e => setForm({...form, state: e.target.value})}
              className="px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <input placeholder="ZIP Code" value={form.zipCode} onChange={e => setForm({...form, zipCode: e.target.value})}
              className="px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
            <input placeholder="Country" value={form.country} onChange={e => setForm({...form, country: e.target.value})}
              className="px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
          </div>
          <div className="flex gap-2">
            <button onClick={handleSave} disabled={saving}
              className="flex-1 py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-bold rounded-xl transition-colors">
              {saving ? 'Saving...' : 'Save Address'}
            </button>
            <button onClick={() => setShowNew(false)}
              className="px-5 py-2.5 border border-gray-200 text-gray-600 text-sm font-semibold rounded-xl hover:bg-gray-50 transition-colors">
              Cancel
            </button>
          </div>
        </div>
      ) : (
        <button onClick={() => setShowNew(true)}
          className="w-full p-4 rounded-2xl border-2 border-dashed border-gray-200 text-gray-500 hover:border-emerald-300 hover:text-emerald-600 hover:bg-emerald-50/30 transition-all flex items-center justify-center gap-2 text-sm font-bold">
          <Plus className="w-4 h-4" /> Add New Address
        </button>
      )}

      <button onClick={onNext} disabled={!selectedAddress}
        className="w-full py-4 bg-emerald-600 hover:bg-emerald-700 disabled:bg-gray-200 disabled:text-gray-400 text-white font-extrabold rounded-2xl flex items-center justify-center gap-2 transition-all shadow-lg shadow-emerald-600/25 active:scale-95">
        Continue to Review <ChevronRight className="w-5 h-5" />
      </button>
    </div>
  );
};

// ── Step 2: Order Review ────────────────────────────────────────────────────────
const ReviewStep = ({ cart, selectedAddress, coupon, setCoupon, discount, setDiscount, onNext, onBack }) => {
  const [couponInput, setCouponInput] = useState('');
  const [couponError, setCouponError] = useState('');

  const subtotal = cart.reduce((sum, item) => sum + (item.discountedPrice ?? item.price) * item.quantity, 0);
  const delivery = subtotal >= 100 ? 0 : 10;
  const tax = subtotal * 0.05;
  const total = subtotal + delivery + tax;

  const applyCoupon = () => {
    const code = couponInput.trim().toUpperCase();
    setCouponError(code ? 'Coupons are not currently supported.' : 'Enter a coupon code.');
    setCoupon('');
    setDiscount(0);
  };

  return (
    <div className="space-y-5">
      <h2 className="text-xl font-black text-gray-900">Review Your Order</h2>

      {/* Items */}
      <div className="space-y-3">
        {cart.map((item, idx) => (
          <div key={idx} className="flex items-center gap-3 bg-white rounded-2xl border border-gray-100 p-3 shadow-sm">
            <img src={item.productImage || 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=80'}
              alt={item.productName} className="w-14 h-14 rounded-xl object-cover border border-gray-100" />
            <div className="flex-1 min-w-0">
              <p className="font-bold text-sm text-gray-900 line-clamp-1">{item.productName}</p>
              <p className="text-xs text-gray-500">Qty: {item.quantity}</p>
            </div>
            <p className="font-black text-gray-900 text-sm">${((item.discountedPrice ?? item.price) * item.quantity).toFixed(2)}</p>
          </div>
        ))}
      </div>

      {/* Coupon */}
      <div className="bg-white rounded-2xl border border-gray-100 p-4">
        <h3 className="text-sm font-bold text-gray-700 mb-3 flex items-center gap-2">
          <Tag className="w-4 h-4 text-emerald-600" /> Apply Coupon
        </h3>
        {coupon ? (
          <div className="flex items-center gap-2 px-4 py-2.5 bg-emerald-50 border border-emerald-200 rounded-xl">
            <CheckCircle2 className="w-4 h-4 text-emerald-600" />
            <span className="text-sm font-bold text-emerald-700">{coupon} — {discount}% OFF applied!</span>
            <button onClick={() => { setCoupon(''); setDiscount(0); setCouponInput(''); }} className="ml-auto text-xs text-gray-500 hover:text-red-500">Remove</button>
          </div>
        ) : (
          <>
            <div className="flex gap-2">
              <input value={couponInput} onChange={e => setCouponInput(e.target.value)}
                onKeyDown={e => e.key === 'Enter' && applyCoupon()}
                placeholder="Coupon codes are not available yet"
                className="flex-1 px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
              <button onClick={applyCoupon}
                className="px-4 py-2.5 bg-emerald-600 text-white text-sm font-bold rounded-xl hover:bg-emerald-700 transition-colors">
                Apply
              </button>
            </div>
            {couponError && <p className="text-xs text-red-500 mt-1 flex items-center gap-1"><AlertCircle className="w-3 h-3" />{couponError}</p>}
          </>
        )}
      </div>

      {/* Price Summary */}
      <div className="bg-white rounded-2xl border border-gray-100 p-4 space-y-3">
        <div className="flex justify-between text-sm text-gray-600"><span>Subtotal</span><span className="font-semibold">${subtotal.toFixed(2)}</span></div>
        <div className="flex justify-between text-sm text-gray-600">
          <span className="flex items-center gap-1"><Truck className="w-3.5 h-3.5" /> Delivery</span>
          <span className="font-semibold">{delivery === 0 ? <span className="text-emerald-600 font-bold">FREE</span> : `$${delivery.toFixed(2)}`}</span>
        </div>
        <div className="flex justify-between text-sm text-gray-600"><span>Estimated tax (5%)</span><span className="font-semibold">${tax.toFixed(2)}</span></div>
        <div className="pt-3 border-t border-gray-100 flex justify-between items-center">
          <span className="font-black text-gray-900">Total</span>
          <span className="text-2xl font-black text-emerald-700">${total.toFixed(2)}</span>
        </div>
      </div>

      {/* Shipping to */}
      <div className="bg-blue-50 border border-blue-100 rounded-2xl p-4 flex items-start gap-3">
        <MapPin className="w-4 h-4 text-blue-600 mt-0.5 shrink-0" />
        <div>
          <p className="text-xs font-bold text-blue-700 uppercase tracking-wide mb-0.5">Delivering to</p>
          <p className="text-sm text-blue-900 font-semibold">{selectedAddress?.streetAddress}, {selectedAddress?.city}, {selectedAddress?.state} {selectedAddress?.zipCode}</p>
        </div>
      </div>

      <div className="flex gap-3">
        <button onClick={onBack} className="px-5 py-4 border border-gray-200 rounded-2xl font-bold text-gray-600 hover:bg-gray-50 flex items-center gap-2 text-sm transition-colors">
          <ChevronLeft className="w-4 h-4" /> Back
        </button>
        <button onClick={onNext}
          className="flex-1 py-4 bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold rounded-2xl flex items-center justify-center gap-2 transition-all shadow-lg shadow-emerald-600/25 active:scale-95 text-sm">
          Continue to Payment <ChevronRight className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
};

// ── Step 3: Payment ─────────────────────────────────────────────────────────────
const PaymentStep = ({ cart, selectedAddress, discount, onBack }) => {
  const [method, setMethod] = useState('CARD');
  const [cardNum, setCardNum] = useState('');
  const [expiry, setExpiry] = useState('');
  const [cvv, setCvv] = useState('');
  const [upiId, setUpiId] = useState('');
  const [placing, setPlacing] = useState(false);
  const navigate = useNavigate();
  const { clearCart } = useCart();
  const { addToast } = useToast();

  const subtotal = cart.reduce((sum, item) => sum + (item.discountedPrice ?? item.price) * item.quantity, 0);
  const delivery = subtotal >= 100 ? 0 : 10;
  const tax = subtotal * 0.05;
  const total = subtotal + delivery + tax;

  const formatCard = (val) => val.replace(/\D/g,'').slice(0,16).replace(/(.{4})/g,'$1 ').trim();
  const formatExpiry = (val) => { const v = val.replace(/\D/g,'').slice(0,4); return v.length >= 3 ? v.slice(0,2)+'/'+v.slice(2) : v; };

  const handlePlaceOrder = async () => {
    if (method === 'CARD' && (!cardNum || !expiry || !cvv)) {
      addToast('Please fill all card details', 'error'); return;
    }
    if (method === 'UPI' && !upiId) {
      addToast('Please enter UPI ID', 'error'); return;
    }

    setPlacing(true);
    try {
      const orderData = {
        recipientName: selectedAddress.fullName,
        recipientPhone: selectedAddress.phone,
        streetAddress: selectedAddress.streetAddress,
        city: selectedAddress.city,
        state: selectedAddress.state,
        zipCode: selectedAddress.zipCode,
        country: selectedAddress.country,
        paymentMethod: method,
        cardNumber: method === 'CARD' ? cardNum.replace(/\s/g, '') : undefined,
        cardExpiry: method === 'CARD' ? expiry : undefined,
        cardCvv: method === 'CARD' ? cvv : undefined,
        upiId: method === 'UPI' ? upiId : undefined,
        items: cart.map(item => ({
          productId: item.productId,
          productName: item.productName,
          productImage: item.productImage,
          price: item.price,
          discountedPrice: item.discountedPrice,
          quantity: item.quantity,
        })),
      };
      const res = await orderService.createOrder(orderData);
      const orderId = res?.data?.orderNumber || res?.data?.id || res?.id;
      await clearCart();
      navigate('/order-confirmation', { state: { orderId, total, orderData: res?.data || orderData } });
    } catch (err) {
      addToast(err.message || 'Unable to place your order. Please try again.', 'error');
    } finally {
      setPlacing(false);
    }
  };

  const METHODS = [
    { id: 'CARD', label: 'Credit / Debit Card', emoji: '💳' },
    { id: 'UPI', label: 'UPI', emoji: '📱' },
    { id: 'CASH_ON_DELIVERY', label: 'Cash on Delivery', emoji: '💵' },
  ];

  return (
    <div className="space-y-5">
      <h2 className="text-xl font-black text-gray-900">Payment Method</h2>

      <div className="space-y-2">
        {METHODS.map(m => (
          <button key={m.id} onClick={() => setMethod(m.id)}
            className={`w-full text-left p-4 rounded-2xl border-2 transition-all flex items-center gap-3 ${
              method === m.id ? 'border-emerald-500 bg-emerald-50/50' : 'border-gray-200 bg-white hover:border-gray-300'
            }`}>
            <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center shrink-0 ${method === m.id ? 'border-emerald-500 bg-emerald-500' : 'border-gray-300'}`}>
              {method === m.id && <div className="w-2 h-2 bg-white rounded-full" />}
            </div>
            <span className="text-lg">{m.emoji}</span>
            <span className="font-bold text-gray-800 text-sm">{m.label}</span>
          </button>
        ))}
      </div>

      {method === 'CARD' && (
        <div className="bg-white rounded-2xl border border-gray-100 p-5 space-y-3">
          <div>
            <label className="text-xs font-bold text-gray-600 uppercase tracking-wide mb-1 block">Card Number</label>
            <input value={cardNum} onChange={e => setCardNum(formatCard(e.target.value))}
              placeholder="1234 5678 9012 3456" maxLength={19}
              className="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 font-mono" />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-bold text-gray-600 uppercase tracking-wide mb-1 block">Expiry</label>
              <input value={expiry} onChange={e => setExpiry(formatExpiry(e.target.value))}
                placeholder="MM/YY" maxLength={5}
                className="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 font-mono" />
            </div>
            <div>
              <label className="text-xs font-bold text-gray-600 uppercase tracking-wide mb-1 block">CVV</label>
              <input value={cvv} onChange={e => setCvv(e.target.value.replace(/\D/g,'').slice(0,4))}
                placeholder="•••" maxLength={4} type="password"
                className="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500 font-mono" />
            </div>
          </div>
        </div>
      )}

      {method === 'UPI' && (
        <div className="bg-white rounded-2xl border border-gray-100 p-5">
          <label className="text-xs font-bold text-gray-600 uppercase tracking-wide mb-1 block">UPI ID</label>
          <input value={upiId} onChange={e => setUpiId(e.target.value)}
            placeholder="yourname@upi"
            className="w-full px-4 py-3 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-4 focus:ring-emerald-500/10 focus:border-emerald-500" />
        </div>
      )}

      {method === 'CASH_ON_DELIVERY' && (
        <div className="bg-amber-50 border border-amber-100 rounded-2xl p-4 flex items-start gap-3">
          <AlertCircle className="w-4 h-4 text-amber-600 mt-0.5 shrink-0" />
          <p className="text-sm text-amber-800 font-medium">Pay with cash when your order is delivered. No prepayment needed.</p>
        </div>
      )}

      <div className="bg-white rounded-2xl border border-gray-100 p-4 flex justify-between items-center">
        <div>
          <p className="text-sm text-gray-600">Order Total</p>
          <p className="text-2xl font-black text-emerald-700">${total.toFixed(2)}</p>
        </div>
        <div className="flex items-center gap-1 text-xs text-gray-400">
          <Shield className="w-3.5 h-3.5" /> Secure Checkout
        </div>
      </div>

      <div className="flex gap-3">
        <button onClick={onBack} className="px-5 py-4 border border-gray-200 rounded-2xl font-bold text-gray-600 hover:bg-gray-50 flex items-center gap-2 text-sm transition-colors">
          <ChevronLeft className="w-4 h-4" /> Back
        </button>
        <button onClick={handlePlaceOrder} disabled={placing}
          className="flex-1 py-4 bg-emerald-600 hover:bg-emerald-700 disabled:bg-emerald-400 text-white font-extrabold rounded-2xl flex items-center justify-center gap-2 transition-all shadow-lg shadow-emerald-600/25 active:scale-95 text-sm">
          {placing ? <><Loader2 className="w-4 h-4 animate-spin" /> Placing Order...</> : <><CheckCircle2 className="w-4 h-4" /> Place Order</>}
        </button>
      </div>
    </div>
  );
};

// ── Main Checkout Page ──────────────────────────────────────────────────────────
const Checkout = () => {
  const { cart } = useCart();
  const items = cart.items || [];
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [selectedAddress, setSelectedAddress] = useState(null);
  const [coupon, setCoupon] = useState('');
  const [discount, setDiscount] = useState(0);

  useEffect(() => {
    if (!isAuthenticated) { navigate('/login'); return; }
    if (items.length === 0) { navigate('/cart'); return; }
  }, [isAuthenticated, items.length, navigate]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-emerald-50/20 to-slate-50 py-10">
      <div className="max-w-2xl mx-auto px-4 sm:px-6">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-2 px-4 py-1.5 bg-emerald-50 text-emerald-700 rounded-full text-xs font-bold border border-emerald-100 mb-3">
            <ShoppingBag className="w-3.5 h-3.5" /> Secure Checkout
          </div>
          <h1 className="text-3xl font-black text-gray-900 tracking-tight">Complete Your Order</h1>
        </div>

        <StepIndicator currentStep={step} />

        <div className="bg-white/80 backdrop-blur-sm rounded-3xl border border-gray-200/60 shadow-xl shadow-gray-200/50 p-6 sm:p-8">
          {step === 1 && (
            <AddressStep
              selectedAddress={selectedAddress}
              setSelectedAddress={setSelectedAddress}
              onNext={() => setStep(2)}
            />
          )}
          {step === 2 && (
            <ReviewStep
              cart={items}
              selectedAddress={selectedAddress}
              coupon={coupon}
              setCoupon={setCoupon}
              discount={discount}
              setDiscount={setDiscount}
              onNext={() => setStep(3)}
              onBack={() => setStep(1)}
            />
          )}
          {step === 3 && (
            <PaymentStep
              cart={items}
              selectedAddress={selectedAddress}
              discount={discount}
              onBack={() => setStep(2)}
            />
          )}
        </div>

        {/* Trust badges */}
        <div className="flex items-center justify-center gap-6 mt-8 text-xs text-gray-400 flex-wrap">
          {['🔒 SSL Secured', '🛡️ Buyer Protection', '↩️ Easy Returns'].map(b => (
            <span key={b} className="font-semibold">{b}</span>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Checkout;
