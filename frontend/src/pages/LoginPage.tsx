import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { login } from '@/api/client';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

export function LoginPage() {
  const [email, setEmail] = useState('admin@northstar.demo');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const qc = useQueryClient();
  const m = useMutation({
    mutationFn: () => login(email, password),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['me'] }); navigate('/'); },
  });
  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <Card className="w-full max-w-sm">
        <CardHeader><CardTitle className="text-lg">SupplyChainOS</CardTitle></CardHeader>
        <CardContent>
          <p className="mb-4 text-sm text-slate-500">Northstar Commerce · Synthetic demo data. Dev seed: admin@northstar.demo</p>
          <form className="space-y-3" onSubmit={(e) => { e.preventDefault(); m.mutate(); }}>
            <label className="block text-sm">Email
              <input className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2" value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
            </label>
            <label className="block text-sm">Password
              <input className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2" value={password} onChange={(e) => setPassword(e.target.value)} type="password" required />
            </label>
            {m.isError && <p className="text-sm text-red-600">{(m.error as Error).message}</p>}
            <Button className="w-full" disabled={m.isPending}>{m.isPending ? 'Signing in…' : 'Sign in'}</Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
