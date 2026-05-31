import { createBrowserRouter, Outlet } from "react-router-dom";
import Header from "../components/layout/header";
import Footer from "../components/layout/footer";
import Home from "../pages/home";
import Pokedex from "../pages/pokedex";
import PokemonDetail from "../pages/pokemonDetail";
import Login from "../pages/login";
import Register from "../pages/register";
import ErrorPage from "../pages/error";
import NotFound from "../pages/notFound";
import ProtectedRoute from "../components/common/protectedRoute";

// Wraps all pages with the shared header and footer
function RootLayout() {
  return (
    <>
      <Header />
      <Outlet />
      <Footer />
    </>
  );
}

// Application routes — add new pages here as the project grows
export const router = createBrowserRouter([
  {
    element: <RootLayout />,
    errorElement: <ErrorPage />,
    children: [
      // Public routes
      {
        path: "/",
        element: <Home />,
      },
      {
        path: "/pokedex",
        element: <Pokedex />,
      },
      {
        path: "/pokedex/:id",
        element: <PokemonDetail />,
      },

      // Guest-only routes (redirect to home if already logged in)
      {
        path: "/login",
        element: (
          <ProtectedRoute requireAuth={false}>
            <Login />
          </ProtectedRoute>
        ),
      },
      {
        path: "/register",
        element: (
          <ProtectedRoute requireAuth={false}>
            <Register />
          </ProtectedRoute>
        ),
      },

      // 404 catch-all
      {
        path: "*",
        element: <NotFound />,
      },
    ],
  },
]);
