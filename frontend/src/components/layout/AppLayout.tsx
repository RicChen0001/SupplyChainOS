import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { fetchMe, logout } from '@/api/client';
import { Button } from '@/components/ui/button';

const NAV = [
  { to: '/', label: 'Overview' },
  { to: '/inventory', label: 'Inventory' },
  { to: '/orders', label: 'Orders' },
  { to: '/shipments', label: 'Shipments' },
  { to: '/risks', label: 'Risk Analysis' },
  { to: '/recommendations', label: 'Recommendations' },
  { to: '/transfers', label: 'Transfers' },
  { to: '/imports', label: 'Data Integration' },
  { to: '/alerts', label: 'Alerts' },
  { to: '/audit-logs', label: 'Audit Logs' },
  { to: '/settings', label: 'Settings' },
];

export function AppLayout() {
  const qc = useQueryClient();
  const navigate = useNavigate();
  const me = useQuery({ queryKey: ['me'], queryFn: fetchMe, retry: false });
  const doLogout = useMutation({
    mutationFn: logout,
    onSettled: () => { qc.clear(); navigate('/login'); },
  });

  return (
    <div className="flex min-h-screen">
      <aside className="hidden w-60 shrink-0 flex-col bg-navy-900 text-white md:flex">
        <div className="px-5 py-5">
          <p className="text-lg font-bold tracking-tight">SupplyChainOS</p>
          <p className="text-xs text-slate-400">Northstar Commerce · Synthetic demo data</p>
        </div>
        <nav className="flex-1 space-y-1 px-3">
          {NAV.map((n) => (
            <NavLink key={n.to} to={n.to} end={n.to === '/'}
              className={({ isActive }) => `block rounded-lg px-3 py-2 text-sm ${isActive ? 'bg-white/10 font-medium' : 'text-slate-300 hover:bg-white/5'}`}>
              {n.label}
            </NavLink>
          ))}
        </nav>
        <div className="border-t border-white/10 px-5 py-4">
          <p className="truncate text-sm">{me.data?.fullName ?? '…'}</p>
          <p className="truncate text-xs text-slate-400">{me.data?.role ?? ''}</p>
          <Button variant="ghost" className="mt-2 px-0 text-slate-300 hover:bg-transparent hover:text-white" onClick={() => doLogout.mutate()}>Sign out</Button>
        </div>
      </aside>
      <div className="flex min-w-0 flex-1 flex-col">
        <header className="border-b border-slate-200 bg-white px-6 py-3">
          <p className="text-sm font-medium text-slate-700">Operations Console</p>
        </header>
        <main className="mx-auto w-full max-w-7xl flex-1 px-4 py-6 sm:px-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
