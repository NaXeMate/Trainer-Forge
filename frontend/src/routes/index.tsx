import { createBrowserRouter } from "react-router";
import Home from "../pages/home";
import Pokedex from "../pages/pokedex";
import PokedexDetail from "../pages/pokedexDetail";
import NotFound from "../pages/notFound";
import Teams from "../pages/teams";

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
    {
        path: "/pokedex/:id",
        element: <PokedexDetail />,
    },

    {
        path: "/teams",
        element: <Teams />,
    },

    // Fallback route
    {
        path: "*",
        element: <NotFound />,
    },
]);
