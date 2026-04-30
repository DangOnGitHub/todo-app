import { useEffect } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from './store/auth';
import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';
import VerifyPage from './pages/VerifyPage';

export default function App() {
  const { isAuthenticated, login, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/');
    }
  }, [isAuthenticated, navigate]);

  if (!isAuthenticated) {
    return (
      <Routes>
        <Route path="/signup" element={<SignUpPage onGoToLogin={() => navigate('/login')} />} />
        <Route path="/verify" element={<VerifyPage onLogin={login} />} />
        <Route path="/" element={<Navigate to="/login" />} />
        <Route
          path="/login"
          element={<LoginPage onLogin={login} onGoToSignUp={() => navigate('/signup')} />}
        />
      </Routes>
    );
  }

  return (
    <div id="app">
      <button type="button" onClick={logout}>
        Log out
      </button>
    </div>
  );
}
