import { useState } from 'react';
import { api } from '../api/client';
import type { components } from '../api/schema';

type AuthResponse = components['schemas']['AuthResponse'];
type Problem = components['schemas']['Problem'];

interface Props {
  onLogin: (token: string) => void;
}

export default function LoginPage({ onLogin }: Props) {
  const [mode, setMode] = useState<'login' | 'register'>('login');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const path = mode === 'login' ? '/auth/login' : '/auth/register';
      const res = await api.post<AuthResponse>(path, { username, password });
      onLogin(res.accessToken);
    } catch (err) {
      const problem = err as Problem;
      setError(problem.detail ?? problem.title ?? 'Something went wrong');
    } finally {
      setLoading(false);
    }
  }

  function toggleMode() {
    setMode(mode === 'login' ? 'register' : 'login');
    setError(null);
  }

  return (
    <div>
      <h1>{mode === 'login' ? 'Sign in' : 'Create account'}</h1>
      <form onSubmit={submit}>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          minLength={3}
          maxLength={50}
          autoComplete="username"
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={8}
          autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
        />
        {error && <p role="alert">{error}</p>}
        <button type="submit" disabled={loading}>
          {loading ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}
        </button>
      </form>
      <button type="button" onClick={toggleMode}>
        {mode === 'login' ? 'Need an account? Register' : 'Already have an account? Sign in'}
      </button>
    </div>
  );
}
