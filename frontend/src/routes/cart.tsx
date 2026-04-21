import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { Trash2, Minus, Plus, ShoppingBag } from "lucide-react";
import { useState } from "react";
import { useCart } from "@/stores/cart";
import { useAuth } from "@/stores/auth";
import { Button } from "@/components/ui/button";
import { api, type Order } from "@/lib/api";
import { toast } from "sonner";

export const Route = createFileRoute("/cart")({
  component: CartPage,
});

function CartPage() {
  const { items, setQty, remove, total, clear } = useCart();
  const { user } = useAuth();
  const nav = useNavigate();
  const [loading, setLoading] = useState(false);

  const checkout = async () => {
    if (!user) {
      toast.info("Please sign in to checkout");
      nav({ to: "/login" });
      return;
    }
    setLoading(true);
    try {
      const payload = {
        userId: user.id,
        userEmail: user.email,
        items: items.map((i) => ({ productId: i.productId, quantity: i.quantity })),
        totalAmount: subtotal + delivery,
      };
      const res = await api.post<Order>("/orders/checkout", payload);
      toast.success(`Order #${res.data.id} placed!`);
      clear();
      nav({ to: "/orders" });
    } catch (e) {
      toast.error((e as Error).message);
    } finally {
      setLoading(false);
    }
  };

  if (items.length === 0) {
    return (
      <div className="mx-auto max-w-3xl px-4 py-20 text-center">
        <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-secondary text-primary">
          <ShoppingBag className="h-10 w-10" />
        </div>
        <h1 className="mt-6 text-2xl font-bold">Your cart is empty</h1>
        <p className="mt-2 text-muted-foreground">Browse our catalog to find what you need.</p>
        <Link to="/catalog"><Button className="mt-6 rounded-full">Shop now</Button></Link>
      </div>
    );
  }

  const subtotal = total();
  const delivery = subtotal > 25 ? 0 : 4.99;

  return (
    <div className="mx-auto max-w-6xl px-4 py-12 md:px-8">
      <h1 className="text-3xl font-bold tracking-tight md:text-4xl">Your cart</h1>
      <div className="mt-8 grid gap-8 lg:grid-cols-[1fr_360px]">
        <div className="space-y-3">
          {items.map((i) => (
            <div key={String(i.productId)} className="flex items-center gap-4 rounded-2xl border border-border/60 bg-card p-4">
              <div className="h-16 w-16 shrink-0 rounded-xl bg-[image:var(--gradient-hero)]" />
              <div className="flex-1">
                <p className="font-semibold">{i.name}</p>
                <p className="text-sm text-muted-foreground">${i.price.toFixed(2)} each</p>
              </div>
              <div className="flex items-center gap-1 rounded-full border border-border bg-background p-1">
                <Button variant="ghost" size="icon" className="h-7 w-7" onClick={() => setQty(i.productId, i.quantity - 1)}>
                  <Minus className="h-3 w-3" />
                </Button>
                <span className="min-w-6 text-center text-sm font-semibold">{i.quantity}</span>
                <Button variant="ghost" size="icon" className="h-7 w-7" onClick={() => setQty(i.productId, i.quantity + 1)}>
                  <Plus className="h-3 w-3" />
                </Button>
              </div>
              <p className="w-20 text-right font-semibold">${(i.price * i.quantity).toFixed(2)}</p>
              <Button variant="ghost" size="icon" onClick={() => remove(i.productId)}>
                <Trash2 className="h-4 w-4 text-muted-foreground" />
              </Button>
            </div>
          ))}
        </div>

        <aside className="h-fit rounded-2xl border border-border/60 bg-card p-6 shadow-[var(--shadow-card)]">
          <h2 className="text-lg font-semibold">Order summary</h2>
          <dl className="mt-4 space-y-2 text-sm">
            <div className="flex justify-between"><dt className="text-muted-foreground">Subtotal</dt><dd>${subtotal.toFixed(2)}</dd></div>
            <div className="flex justify-between"><dt className="text-muted-foreground">Delivery</dt><dd>{delivery === 0 ? "Free" : `$${delivery.toFixed(2)}`}</dd></div>
            <div className="my-3 border-t border-border" />
            <div className="flex justify-between text-base font-bold"><dt>Total</dt><dd>${(subtotal + delivery).toFixed(2)}</dd></div>
          </dl>
          <Button onClick={checkout} disabled={loading} size="lg" className="mt-6 w-full rounded-full shadow-[var(--shadow-glow)]">
            {loading ? "Placing order…" : "Checkout"}
          </Button>
          <p className="mt-3 text-center text-xs text-muted-foreground">Secure payment · 100% authentic</p>
        </aside>
      </div>
    </div>
  );
}
