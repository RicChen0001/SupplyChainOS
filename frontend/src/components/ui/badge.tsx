import { cn } from '@/lib/utils';
export function Badge({ children, tone = 'neutral' }: { children: React.ReactNode; tone?: 'neutral' | 'success' | 'warning' | 'critical' }) {
  return (
    <span className={cn('inline-flex rounded-full px-2 py-0.5 text-xs font-medium',
      tone === 'neutral' && 'bg-slate-100 text-slate-700',
      tone === 'success' && 'bg-emerald-50 text-emerald-700',
      tone === 'warning' && 'bg-amber-50 text-amber-700',
      tone === 'critical' && 'bg-red-50 text-red-700')}>{children}</span>
  );
}
