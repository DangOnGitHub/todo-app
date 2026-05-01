import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api, type ApiSchema } from '../api/client';

type AuthResponse = ApiSchema<'AuthResponse'>;
type Problem = ApiSchema<'Problem'>;

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
        const response = await api.post<AuthResponse>('/auth/verify', { token });
        onLogin(response.accessToken);
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
      <div className="auth-page">
        <h1>Verifying your email…</h1>
        <p>Please wait while we verify your email address.</p>
      </div>
    );
  }

  if (status === 'success') {
    return (
      <div className="auth-page">
        <h1>Email verified!</h1>
        <p>Your email has been verified. Redirecting…</p>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <h1>Verification failed</h1>
      <p role="alert">{error}</p>
      <p>The verification link may have expired or is invalid.</p>
    </div>
  );
}
