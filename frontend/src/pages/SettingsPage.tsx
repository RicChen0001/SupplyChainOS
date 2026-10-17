import { EmptyState } from '@/components/ui/empty-state';
export function SettingsPage() {
  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold tracking-tight">Settings</h1>
      <EmptyState title="Settings ship in a later phase" hint="Phase 1 covers auth + foundation. User/role management and import policies arrive with their modules." />
    </div>
  );
}
