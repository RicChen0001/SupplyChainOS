import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { fetchMe } from '@/api/client';
import { AppLayout } from '@/components/layout/AppLayout';
import { LoginPage } from '@/pages/LoginPage';
import { OverviewPage } from '@/pages/OverviewPage';
import { PlaceholderPage } from '@/pages/PlaceholderPage';
import { SettingsPage } from '@/pages/SettingsPage';

function RequireAuth({ children }: { children: React.ReactNode }) {
  const me = useQuery({ queryKey: ['me'], queryFn: fetchMe, retry: false });
  if (me.isPending) return <p className="p-8 text-sm text-slate-500">Loading…</p>;
  if (me.isError) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<RequireAuth><AppLayout /></RequireAuth>}>
          <Route path="/" element={<OverviewPage />} />
          <Route path="/inventory" element={<PlaceholderPage title="Inventory" phase="Phase 3" />} />
          <Route path="/orders" element={<PlaceholderPage title="Orders" phase="Phase 3" />} />
          <Route path="/shipments" element={<PlaceholderPage title="Shipments" phase="Phase 3" />} />
          <Route path="/risks" element={<PlaceholderPage title="Risk Analysis" phase="Phase 4" />} />
          <Route path="/recommendations" element={<PlaceholderPage title="Recommendations" phase="Phase 4" />} />
          <Route path="/transfers" element={<PlaceholderPage title="Transfers" phase="Phase 5" />} />
          <Route path="/imports" element={<PlaceholderPage title="Data Integration" phase="Phase 2" />} />
          <Route path="/alerts" element={<PlaceholderPage title="Alerts" phase="Phase 5" />} />
          <Route path="/audit-logs" element={<PlaceholderPage title="Audit Logs" phase="Phase 5" />} />
          <Route path="/settings" element={<SettingsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}
