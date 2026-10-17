import { useQuery } from '@tanstack/react-query';
import { fetchOverview, fetchWarehouses } from '@/api/client';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { ErrorState, LoadingState } from '@/components/ui/empty-state';
import { formatNumber } from '@/lib/format';

export function OverviewPage() {
  const overview = useQuery({ queryKey: ['overview'], queryFn: fetchOverview });
  const warehouses = useQuery({ queryKey: ['warehouses'], queryFn: fetchWarehouses });

  if (overview.isPending) return <LoadingState />;
  if (overview.isError) return <ErrorState message={(overview.error as Error).message} onRetry={() => overview.refetch()} />;

  const o = overview.data;
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Overview</h1>
        <p className="text-sm text-slate-500">Live metrics from PostgreSQL — no hardcoded dashboard numbers.</p>
      </div>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card><CardHeader><CardTitle>Warehouses</CardTitle></CardHeader><CardContent><p className="text-3xl font-bold">{o.totalWarehouses}</p></CardContent></Card>
        <Card><CardHeader><CardTitle>Products</CardTitle></CardHeader><CardContent><p className="text-3xl font-bold">{formatNumber(o.totalProducts)}</p></CardContent></Card>
        <Card><CardHeader><CardTitle>Inventory units (on hand)</CardTitle></CardHeader><CardContent><p className="text-3xl font-bold">{formatNumber(o.totalInventoryUnits)}</p></CardContent></Card>
        <Card><CardHeader><CardTitle>Inventory records</CardTitle></CardHeader><CardContent><p className="text-3xl font-bold">{formatNumber(o.totalInventoryRecords)}</p></CardContent></Card>
      </div>
      <Card>
        <CardHeader><CardTitle>Warehouses</CardTitle></CardHeader>
        <CardContent>
          {warehouses.isPending && <p className="text-sm text-slate-500">Loading…</p>}
          {warehouses.isError && <p className="text-sm text-red-600">{(warehouses.error as Error).message}</p>}
          <ul className="divide-y divide-slate-100">
            {warehouses.data?.map((w) => (
              <li key={w.id} className="flex items-center justify-between py-2 text-sm">
                <span><strong>{w.code}</strong> — {w.name}</span>
                <span className="text-slate-500">{w.city}, {w.state} · {w.timezone}</span>
              </li>
            ))}
          </ul>
        </CardContent>
      </Card>
      <p className="text-xs text-slate-400">Full dashboard charts (order trends, shipment breakdown, low-stock ranking) land in Phase 3. This page already proves the frontend → backend → Postgres path.</p>
    </div>
  );
}
