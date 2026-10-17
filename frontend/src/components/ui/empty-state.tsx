export function EmptyState({ title, hint }: { title: string; hint?: string }) {
  return (
    <div className="rounded-xl border border-dashed border-slate-300 bg-white px-6 py-10 text-center">
      <p className="font-medium text-slate-700">{title}</p>
      {hint && <p className="mt-1 text-sm text-slate-500">{hint}</p>}
    </div>
  );
}
export function ErrorState({ message, onRetry }: { message: string; onRetry?: () => void }) {
  return (
    <div className="rounded-xl border border-red-200 bg-red-50 px-6 py-6 text-center">
      <p className="font-medium text-red-800">Something went wrong</p>
      <p className="mt-1 text-sm text-red-700">{message}</p>
      {onRetry && <button onClick={onRetry} className="mt-3 rounded-lg bg-red-700 px-3 py-1.5 text-sm text-white">Retry</button>}
    </div>
  );
}
export function LoadingState() {
  return <div className="rounded-xl border border-slate-200 bg-white px-6 py-10 text-center text-sm text-slate-500" role="status">Loading…</div>;
}
