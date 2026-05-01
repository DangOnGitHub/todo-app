import { useState } from 'react';
import { api, type ApiSchema } from '../api/client';

type AuthResponse = ApiSchema<'AuthResponse'>;
type Problem = ApiSchema<'Problem'>;

interface Props {
  onLogin: (token: string) => void;
  onGoToSignUp: () => void;
}

export default function LoginPage({ onLogin, onGoToSignUp }: Props) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function submit(e: React.SubmitEvent) {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = await api.post<AuthResponse>('/auth/login', { email, password });
      onLogin(response.accessToken);
    } catch (err) {
      const problem = err as Problem;
      setError(problem.detail ?? problem.title ?? 'Something went wrong');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <h1>Login</h1>
      <form onSubmit={submit}>
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          autoComplete="email"
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          autoComplete="current-password"
        />
        <p role="alert" style={{ minHeight: '1.25rem' }}>
          {error}
        </p>
        <button type="submit" disabled={loading}>
          {loading ? 'Please wait…' : 'Login'}
        </button>
      </form>
      <button type="button" onClick={onGoToSignUp}>
        Don't have an account? Sign up
      </button>
    </div>
  );
}
