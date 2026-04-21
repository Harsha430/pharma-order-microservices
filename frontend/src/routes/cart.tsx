import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { Trash2, Minus, Plus, ShoppingBag, Coins } from "lucide-react";
import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useCart } from "@/stores/cart";
import { useAuth } from "@/stores/auth";
import { Button } from "@/components/ui/button";
import { api, type Order } from "@/lib/api";
import { toast } from "sonner";
import { PrescriptionUpload } from "@/components/PrescriptionUpload";

export const Route = createFileRoute("/cart")({
  component: CartPage,
});

function CartPage() {
  const { items, setQty, remove, total, clear, prescriptionId, setPrescription } = useCart();
  const { user } = useAuth();
  const nav = useNavigate();
  const [loading, setLoading] = useState(false);
  const [redeem, setRedeem] = useState(false);
  
  const needsPrescription = items.some(i => i.prescriptionRequired);

  const { data: loyalty } = useQuery({
    queryKey: ["loyalty", user?.id],
    queryFn: async () => (await api.get<{totalPoints: number}>(`/loyalty/user/${user!.id}`)).data,
    enabled: !!user?.id,
  });

  const queryClient = useQueryClient();

  const pointsAvailable = loyalty?.totalPoints ?? 0;
  const MIN_REDEEM = 2000; // minimum health points to redeem
  const canRedeem = pointsAvailable >= MIN_REDEEM;
  // Redeem all available points, capped so discount ≤ subtotal
  const maxRedeemablePoints = Math.min(pointsAvailable, Math.floor(total() * 10));
  const discount = redeem && canRedeem ? maxRedeemablePoints / 10 : 0;

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
        orderItems: items.map((i) => ({ productId: i.productId, quantity: i.quantity, price: i.price })),
        totalAmount: subtotal + delivery - discount,
        pointsRedeemed: redeem ? maxRedeemablePoints : 0,
        discountAmount: discount,
        prescriptionId: useCart.getState().prescriptionId,
      };
      const res = await api.post<Order>("/orders/checkout", payload);
      queryClient.invalidateQueries({ queryKey: ["loyalty", user.id] });
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
                <p className="text-sm text-muted-foreground">₹{i.price.toFixed(2)} each</p>
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
              <p className="w-20 text-right font-semibold">₹{(i.price * i.quantity).toFixed(2)}</p>
              <Button variant="ghost" size="icon" onClick={() => remove(i.productId)}>
                <Trash2 className="h-4 w-4 text-muted-foreground" />
              </Button>
            </div>
          ))}
        </div>

        <aside className="h-fit rounded-2xl border border-border/60 bg-card p-6 shadow-[var(--shadow-card)]">
          <h2 className="text-lg font-semibold">Order summary</h2>
          <dl className="mt-4 space-y-2 text-sm">
            <div className="flex justify-between"><dt className="text-muted-foreground">Subtotal</dt><dd>₹{subtotal.toFixed(2)}</dd></div>
            <div className="flex justify-between"><dt className="text-muted-foreground">Delivery</dt><dd>{delivery === 0 ? "Free" : `₹${delivery.toFixed(2)}`}</dd></div>
            
            {pointsAvailable >= 0 && (
              <div className="mt-4 rounded-xl bg-primary/5 border border-primary/10 p-3">
                <div className="flex items-center gap-2 mb-2">
                  <Coins className="h-4 w-4 text-primary shrink-0" />
                  <span className="text-xs font-semibold text-foreground">
                    {pointsAvailable} Health Points
                  </span>
                  <span className="ml-auto text-[10px] text-muted-foreground">10pts = ₹1</span>
                </div>

                {canRedeem ? (
                  <div className="flex items-center justify-between">
                    <span className="text-[11px] text-muted-foreground">
                      Redeem for ₹{(maxRedeemablePoints / 10).toFixed(2)} off
                    </span>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-6 px-2 text-xs text-primary"
                      onClick={() => setRedeem(!redeem)}
                    >
                      {redeem ? "Cancel" : "Redeem"}
                    </Button>
                  </div>
                ) : (
                  <p className="text-[11px] text-muted-foreground">
                    Min. <span className="font-semibold text-foreground">2,000 points</span> to redeem
                    {pointsAvailable < 2000 && ` (need ${2000 - pointsAvailable} more)`}
                  </p>
                )}

                {redeem && canRedeem && (
                  <div className="mt-2 text-[10px] text-success-fg font-medium">
                    ✓ Using {maxRedeemablePoints} pts → ₹{discount.toFixed(2)} discount applied
                  </div>
                )}

                <p className="mt-2 text-[10px] text-muted-foreground/70 border-t border-border/30 pt-2">
                  You'll earn <span className="font-semibold text-foreground">{Math.floor((subtotal - discount) / 100)}</span> points from this order
                </p>
              </div>
            )}

            {discount > 0 && (
              <div className="flex justify-between text-success-fg"><dt>Points Discount</dt><dd>-₹{discount.toFixed(2)}</dd></div>
            )}

            <div className="my-3 border-t border-border" />
            <div className="flex justify-between text-base font-bold"><dt>Total</dt><dd>₹{(subtotal + delivery - discount).toFixed(2)}</dd></div>
          </dl>

          {needsPrescription && (
            <div className="mt-6 border-t border-border pt-6">
              <PrescriptionUpload onSuccess={(id) => setPrescription(id)} />
            </div>
          )}

          <Button 
            onClick={checkout} 
            disabled={loading || (needsPrescription && !prescriptionId)} 
            size="lg" 
            className="mt-6 w-full rounded-full shadow-[var(--shadow-glow)]"
          >
            {loading ? "Placing order…" : (needsPrescription && !prescriptionId ? "Upload Prescription" : "Checkout")}
          </Button>
          <p className="mt-3 text-center text-xs text-muted-foreground">Secure payment · 100% authentic</p>
        </aside>
      </div>
    </div>
  );
}
