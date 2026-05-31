import { Navigate } from "react-router-dom";
import { useAuth } from "../../../context/AuthContext";
import type { ReactNode } from "react";

type ProtectedRouteProps = {
  children: ReactNode;
  requireAuth: boolean; // true = only logged-in users; false = only guests
};

// Redirects users based on their auth status
function ProtectedRoute({ children, requireAuth }: ProtectedRouteProps) {
  const { user, isLoading } = useAuth();

  // Wait until auth state is resolved before redirecting
  if (isLoading) {
    return <div>Loading...</div>;
  }

  // If auth is required but user is not logged in, go to login
  if (requireAuth && !user) {
    return <Navigate to="/login" replace />;
  }

  // If the route is for guests only but user is logged in, go to home
  if (!requireAuth && user) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}

export default ProtectedRoute;
