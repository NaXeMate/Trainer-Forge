import { createBrowserRouter } from "react-router";
import Home from "../pages/home";
import Pokedex from "../pages/pokedex";
import NotFound from "../pages/notFound";

export const router = createBrowserRouter([
    // Public routes
    {
        path: "/",
        element: <Home />,
    },
    {
        path: "/pokedex",
        element: <Pokedex />,
    },

    // Fallback route
    {
        path: "*",
        element: <NotFound />,
    },
]);
