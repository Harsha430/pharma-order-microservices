import { Link, useNavigate } from "@tanstack/react-router";
import { ShoppingCart, User, LogOut, Pill, Menu, X } from "lucide-react";
import { useState, useEffect } from "react";
import { useAuth } from "@/stores/auth";
import { useCart } from "@/stores/cart";
import { Button } from "@/components/ui/button";

export function Header() {
  const { user, logout } = useAuth();
  const count = useCart((s) => s.count());
  const nav = useNavigate();
  const [open, setOpen] = useState(false);
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
    // If we have a token but no user ID (stale session), refresh the profile
    const token = useAuth.getState().token;
    if (token && !user?.id) {
      import("@/lib/api").then(({ api }) => {
        api.get("/users/me").then((res: any) => {
          useAuth.getState().setAuth(String(res.data.id), token, res.data.email, res.data.roles);
        }).catch(() => {
          // If profile fetch fails, session might be invalid
          logout();
        });
      });
    }
  }, [user?.id, logout]);

  const links = [
    { to: "/", label: "Home" },
    { to: "/catalog", label: "Catalog" },
    { to: "/inventory", label: "Stock" },
    { to: "/orders", label: "Orders" },
  ] as const;

  return (
    <header className="sticky top-0 z-40 border-b border-border/60 bg-background/80 backdrop-blur-xl">
      <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-3 md:px-8">
        <Link to="/" className="flex items-center gap-2">
          <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-[image:var(--gradient-primary)] text-primary-foreground shadow-[var(--shadow-glow)]">
            <Pill className="h-5 w-5" />
          </span>
          <span className="text-lg font-semibold tracking-tight">
            Pharma<span className="text-primary">Order</span>
          </span>
        </Link>

        <nav className="hidden items-center gap-1 md:flex">
          {links.map((l) => (
            <Link
              key={l.to}
              to={l.to}
              activeOptions={{ exact: l.to === "/" }}
              activeProps={{ className: "bg-secondary text-secondary-foreground" }}
              className="rounded-lg px-3 py-2 text-sm font-medium text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground"
            >
              {l.label}
            </Link>
          ))}
        </nav>

        <div className="flex items-center gap-2">
          <Link to="/cart" className="relative">
            <Button variant="ghost" size="icon" aria-label="Cart">
              <ShoppingCart className="h-5 w-5" />
            </Button>
            {mounted && count > 0 && (
              <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-accent px-1 text-[10px] font-bold text-accent-foreground">
                {count}
              </span>
            )}
          </Link>

          {user ? (
            <div className="hidden items-center gap-1 md:flex">
              <Link to="/profile">
                <Button variant="ghost" size="icon"><User className="h-5 w-5" /></Button>
              </Link>
              <Button variant="ghost" size="icon" onClick={() => { logout(); nav({ to: "/" }); }}>
                <LogOut className="h-5 w-5" />
              </Button>
            </div>
          ) : (
            <Link to="/login" className="hidden md:block">
              <Button variant="default" className="rounded-full px-5">Sign in</Button>
            </Link>
          )}

          <Button variant="ghost" size="icon" className="md:hidden" onClick={() => setOpen((o) => !o)}>
            {open ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </Button>
        </div>
      </div>

      {open && (
        <div className="border-t border-border/60 bg-background md:hidden">
          <nav className="flex flex-col p-4">
            {links.map((l) => (
              <Link
                key={l.to}
                to={l.to}
                onClick={() => setOpen(false)}
                className="rounded-lg px-3 py-2 text-sm font-medium hover:bg-secondary"
              >
                {l.label}
              </Link>
            ))}
            {user ? (
              <>
                <Link to="/profile" onClick={() => setOpen(false)} className="rounded-lg px-3 py-2 text-sm font-medium hover:bg-secondary">Profile</Link>
                <button onClick={() => { logout(); setOpen(false); nav({ to: "/" }); }} className="rounded-lg px-3 py-2 text-left text-sm font-medium hover:bg-secondary">Logout</button>
              </>
            ) : (
              <Link to="/login" onClick={() => setOpen(false)} className="rounded-lg px-3 py-2 text-sm font-medium text-primary">Sign in</Link>
            )}
          </nav>
        </div>
      )}
    </header>
  );
}
