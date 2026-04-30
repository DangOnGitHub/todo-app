import { useState } from 'react';
import { api } from '../api/client';
import type { components } from '../api/schema';

type SignUpResponse = components['schemas']['SignUpResponse'];
type Problem = components['schemas']['Problem'];

interface Props {
  onGoToLogin: () => void;
}

export default function SignUpPage({ onGoToLogin }: Props) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [repeatPassword, setRepeatPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [verificationSent, setVerificationSent] = useState(false);

  async function submit(e: React.SubmitEvent) {
    e.preventDefault();
    if (password !== repeatPassword) {
      setError('Passwords do not match');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await api.post<SignUpResponse>('/auth/signup', { email, password });
      setVerificationSent(true);
    } catch (err) {
      const problem = err as Problem;
      setError(problem.detail ?? problem.title ?? 'Something went wrong');
    } finally {
      setLoading(false);
    }
  }

  if (verificationSent) {
    return (
      <div>
        <h1>Check your email</h1>
        <p>
          We sent a verification link to <strong>{email}</strong>.
        </p>
        <p>Click the link in the email to verify your account and sign in.</p>
        <button type="button" onClick={onGoToLogin}>
          Back to login
        </button>
      </div>
    );
  }

  return (
    <div>
      <h1>Sign up</h1>
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
