import { EmptyState } from '@/components/ui/empty-state';
export function PlaceholderPage({ title, phase }: { title: string; phase: string }) {
  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold tracking-tight">{title}</h1>
      <EmptyState title="Not yet implemented" hint={`This module ships in ${phase}. Its backend API and UI are intentionally not faked — this page is a placeholder, not a demo of finished behaviour.`} />
    </div>
  );
}
