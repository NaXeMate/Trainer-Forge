import { createBrowserRouter } from "react-router";

export const router = createBrowserRouter([
    // Public routes
    {
        path: "/",
        lazy: async () => {
            const { default: Component } = await import("../pages/home");
            return { Component };
        },
    },
    {
        path: "/pokedex",
        lazy: async () => {
            const { default: Component } = await import("../pages/pokedex");
            return { Component };
        },
    },
    {
        path: "/pokedex/:id",
        lazy: async () => {
            const { default: Component } = await import("../pages/pokedexDetail");
            return { Component };
        },
    },

    {
        path: "/teams",
        lazy: async () => {
            const { default: Component } = await import("../pages/teams");
            return { Component };
        },
    },
    {
        path: "/simulator",
        lazy: async () => {
            const { default: Component } = await import("../pages/simulator");
            return { Component };
        },
    },
    {
        path: "/trainer",
        lazy: async () => {
            const { default: Component } = await import("../pages/trainer");
            return { Component };
        },
    },

    // Fallback route
    {
        path: "*",
        lazy: async () => {
            const { default: Component } = await import("../pages/notFound");
            return { Component };
        },
    },
]);
