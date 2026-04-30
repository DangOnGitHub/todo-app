import { useState } from 'react';
import { useAuth } from './store/auth';
import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';

export default function App() {
  const { isAuthenticated, login, logout } = useAuth();
  const [page, setPage] = useState<'login' | 'signup'>('login');

  if (!isAuthenticated) {
    if (page === 'signup') {
      return <SignUpPage onLogin={login} onGoToLogin={() => setPage('login')} />;
    }
    return <LoginPage onLogin={login} onGoToSignUp={() => setPage('signup')} />;
  }

  return (
    <div id="app">
      <button type="button" onClick={logout}>
        Log out
      </button>
    </div>
  );
}
