import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import AppLayout from '../components/layout/AppLayout';
import LoginPage from '../pages/LoginPage';
import WorkspacesPage from '../pages/workspaces/WorkspacesPage';
import WorkspaceDetailPage from '../pages/workspaces/WorkspaceDetailPage';
import BillingPage from '../pages/billing/BillingPage';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { token } = useAuth();
  return token ? <>{children}</> : <Navigate to="/login" replace />;
}

export default function AppRouter() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/workspaces" replace />} />
        <Route path="workspaces" element={<WorkspacesPage />} />
        <Route path="workspaces/:id" element={<WorkspaceDetailPage />} />
        <Route path="billing" element={<BillingPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
