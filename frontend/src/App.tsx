import { useAuth } from './store/auth';
import LoginPage from './pages/LoginPage';

export default function App() {
  const { isAuthenticated, login } = useAuth();

  if (!isAuthenticated) {
    return <LoginPage onLogin={login} />;
  }

  return <div id="app" />;
}
