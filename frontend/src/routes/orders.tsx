import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { Package, Clock } from "lucide-react";
import { api, type Order } from "@/lib/api";
import { useAuth } from "@/stores/auth";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/orders")({
  component: OrdersPage,
});

function OrdersPage() {
  const { user } = useAuth();

  const { data: orders = [], isLoading } = useQuery({
    queryKey: ["orders", user?.id],
    queryFn: async () => {
      const res = await api.get<Order[]>(`/orders/user/${user!.id}`);
      return res.data;
    },
    enabled: !!user?.id,
    retry: false,
  });

  if (!user) {
    return (
      <div className="mx-auto max-w-2xl px-4 py-20 text-center">
        <h1 className="text-2xl font-bold">Sign in to view your orders</h1>
        <Link to="/login"><Button className="mt-6 rounded-full">Sign in</Button></Link>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-12 md:px-8">
      <h1 className="text-3xl font-bold tracking-tight md:text-4xl">Your orders</h1>
      <p className="mt-1 text-muted-foreground">Track and revisit your previous purchases.</p>

      {isLoading && <p className="mt-8 text-muted-foreground">Loading…</p>}

      {!isLoading && orders.length === 0 && (
        <div className="mt-12 rounded-2xl border border-dashed border-border/60 p-12 text-center">
          <Package className="mx-auto h-10 w-10 text-muted-foreground" />
          <p className="mt-4 font-semibold">No orders yet</p>
          <p className="mt-1 text-sm text-muted-foreground">When you place an order it will show up here.</p>
          <Link to="/catalog"><Button className="mt-5 rounded-full">Start shopping</Button></Link>
        </div>
      )}

      <div className="mt-8 space-y-3">
        {orders.map((o) => (
          <div key={String(o.id)} className="flex flex-wrap items-center justify-between gap-4 rounded-2xl border border-border/60 bg-card p-5 shadow-[var(--shadow-card)]">
            <div className="flex items-center gap-4">
              <span className="flex h-12 w-12 items-center justify-center rounded-xl bg-secondary text-primary">
                <Package className="h-6 w-6" />
              </span>
              <div>
                <p className="font-semibold">Order #{o.id}</p>
                <p className="flex items-center gap-1 text-xs text-muted-foreground">
                  <Clock className="h-3 w-3" /> {o.createdAt ?? "Recent"}
                </p>
              </div>
            </div>
            <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-semibold text-primary">{o.status}</span>
            <p className="text-lg font-bold">${o.totalAmount?.toFixed(2)}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
