import { useState } from 'react';
import { api } from '../api/client';
import type { components } from '../api/schema';

type AuthResponse = components['schemas']['AuthResponse'];
type Problem = components['schemas']['Problem'];

interface Props {
  onLogin: (token: string) => void;
  onGoToLogin: () => void;
}

export default function SignUpPage({ onLogin, onGoToLogin }: Props) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [repeatPassword, setRepeatPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function submit(e: React.SubmitEvent) {
    e.preventDefault();
    if (password !== repeatPassword) {
      setError('Passwords do not match');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      const res = await api.post<AuthResponse>('/auth/signup', { username, password });
      onLogin(res.accessToken);
    } catch (err) {
      const problem = err as Problem;
      setError(problem.detail ?? problem.title ?? 'Something went wrong');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <h1>Sign up</h1>
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
          autoComplete="new-password"
        />
        <input
          type="password"
          placeholder="Repeat password"
          value={repeatPassword}
          onChange={(e) => setRepeatPassword(e.target.value)}
          required
          minLength={8}
          autoComplete="new-password"
        />
        <p role="alert" style={{ minHeight: '1.25rem' }}>
          {error}
        </p>
        <button type="submit" disabled={loading}>
          {loading ? 'Please wait…' : 'Sign up'}
        </button>
      </form>
      <button type="button" onClick={onGoToLogin}>
        Already have an account? Login
      </button>
    </div>
  );
}
