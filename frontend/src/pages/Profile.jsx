import React, { useEffect, useState } from 'react';
import { useToast } from '../context/ToastContext';
import { userService } from '../services/userService';
import { authService } from '../services/authService';

const Profile = () => {
  const { addToast } = useToast();
  const [profile, setProfile] = useState({ name: '', phone: '' });
  const [password, setPassword] = useState({ currentPassword: '', newPassword: '' });
  const [saving, setSaving] = useState(false);

  useEffect(() => { userService.getProfile().then((res) => setProfile(res.data || {})).catch((err) => addToast(err.message, 'error')); }, [addToast]);
  const saveProfile = async (event) => { event.preventDefault(); setSaving(true); try { const res = await userService.updateProfile(profile); setProfile(res.data || profile); addToast('Profile updated', 'success'); } catch (err) { addToast(err.message, 'error'); } finally { setSaving(false); } };
  const changePassword = async (event) => { event.preventDefault(); try { await authService.updatePassword(password.currentPassword, password.newPassword); setPassword({ currentPassword: '', newPassword: '' }); addToast('Password updated', 'success'); } catch (err) { addToast(err.message, 'error'); } };

  return <div className="max-w-3xl mx-auto px-4 py-10 space-y-8"><div><h1 className="text-3xl font-black text-gray-900">My Profile</h1><p className="text-gray-500 mt-1">Keep your account details up to date.</p></div><form onSubmit={saveProfile} className="bg-white rounded-2xl border border-gray-200 p-6 space-y-4"><h2 className="text-lg font-black">Profile details</h2><label className="block text-sm font-bold">Name<input value={profile.name || ''} onChange={(e) => setProfile({ ...profile, name: e.target.value })} className="mt-1 w-full p-3 border rounded-xl" required /></label><label className="block text-sm font-bold">Phone<input value={profile.phone || ''} onChange={(e) => setProfile({ ...profile, phone: e.target.value })} className="mt-1 w-full p-3 border rounded-xl" /></label><button disabled={saving} className="px-5 py-3 rounded-xl bg-emerald-600 text-white font-bold">{saving ? 'Saving…' : 'Save profile'}</button></form><form onSubmit={changePassword} className="bg-white rounded-2xl border border-gray-200 p-6 space-y-4"><h2 className="text-lg font-black">Change password</h2><label className="block text-sm font-bold">Current password<input type="password" value={password.currentPassword} onChange={(e) => setPassword({ ...password, currentPassword: e.target.value })} className="mt-1 w-full p-3 border rounded-xl" required /></label><label className="block text-sm font-bold">New password<input type="password" minLength="6" value={password.newPassword} onChange={(e) => setPassword({ ...password, newPassword: e.target.value })} className="mt-1 w-full p-3 border rounded-xl" required /></label><button className="px-5 py-3 rounded-xl bg-gray-900 text-white font-bold">Update password</button></form></div>;
};

export default Profile;
