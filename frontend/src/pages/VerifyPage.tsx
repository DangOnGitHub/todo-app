import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api } from '../api/client';
import type { components } from '../api/schema';

type AuthResponse = components['schemas']['AuthResponse'];
type Problem = components['schemas']['Problem'];

interface Props {
  onLogin: (token: string) => void;
}

export default function VerifyPage({ onLogin }: Props) {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState<'verifying' | 'success' | 'error'>('verifying');
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function verify() {
      const token = searchParams.get('token');
      if (!token) {
        setError('Invalid verification link: no token provided');
        setStatus('error');
        return;
      }

      try {
        const res = await api.post<AuthResponse>('/auth/verify', { token });
        onLogin(res.accessToken);
        setStatus('success');
      } catch (err) {
        const problem = err as Problem;
        setError(problem.detail ?? problem.title ?? 'Verification failed');
        setStatus('error');
      }
    }

    verify();
  }, [searchParams, onLogin]);

  if (status === 'verifying') {
    return (
      <div>
        <h1>Verifying your email…</h1>
        <p>Please wait while we verify your email address.</p>
      </div>
    );
  }

  if (status === 'success') {
    return (
      <div>
        <h1>Email verified!</h1>
        <p>Your email has been verified. Redirecting…</p>
      </div>
    );
  }

  return (
    <div>
      <h1>Verification failed</h1>
      <p role="alert">{error}</p>
      <p>The verification link may have expired or is invalid.</p>
    </div>
  );
}
