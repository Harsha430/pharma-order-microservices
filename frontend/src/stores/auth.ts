import { create } from "zustand";
import { persist } from "zustand/middleware";

type AuthUser = { id: string; email: string; roles: string[] } | null;

type AuthState = {
  token: string | null;
  user: AuthUser;
  setAuth: (id: string, token: string, email: string, roles: string[]) => void;
  logout: () => void;
};

export const useAuth = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      setAuth: (id, token, email, roles) => {
        if (typeof window !== "undefined") {
          window.localStorage.setItem("pharma_token", token);
        }
        set({ token, user: { id, email, roles } });
      },
      logout: () => {
        if (typeof window !== "undefined") {
          window.localStorage.removeItem("pharma_token");
        }
        set({ token: null, user: null });
      },
    }),
    { name: "pharma_auth" },
  ),
);
