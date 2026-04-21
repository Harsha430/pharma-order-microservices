import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { Trash2, Minus, Plus, ShoppingBag, Coins, Ticket } from "lucide-react";
import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useCart } from "@/stores/cart";
import { useAuth } from "@/stores/auth";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { api, type Order } from "@/lib/api";
import { toast } from "sonner";
import { PrescriptionUpload } from "@/components/PrescriptionUpload";

export const Route = createFileRoute("/cart")({
  component: CartPage,
});

function CartPage() {
  const { items, setQty, remove, total, clear, prescriptionId, setPrescription, appliedBundles, appliedCoupon, applyCoupon } = useCart();
  const { user } = useAuth();
  const nav = useNavigate();
  const [loading, setLoading] = useState(false);
  const [redeem, setRedeem] = useState(false);
  const [couponCode, setCouponCode] = useState("");
  
  const handleApplyCoupon = () => {
    if (!couponCode) return;
    const success = applyCoupon(couponCode);
    if (success) {
      toast.success(`Coupon ${couponCode.toUpperCase()} applied!`);
      setCouponCode("");
    } else {
      toast.error("Invalid coupon code");
    }
  };

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
  
  const itemsTotal = items.reduce((a, i) => a + i.price * i.quantity, 0);
  const bundleDiscount = appliedBundles.reduce((a, b) => a + b.discount, 0);
  
  // Important: calculate coupon discount based on subtotal after bundle discounts
  const subtotalAfterBundles = itemsTotal - bundleDiscount;
  const couponDiscount = appliedCoupon ? (subtotalAfterBundles * (appliedCoupon.discount / 100)) : 0;
  
  // Points discount (after bundles and coupons)
  const subtotalAfterPromo = subtotalAfterBundles - couponDiscount;
  const maxRedeemablePoints = Math.min(pointsAvailable, Math.floor(subtotalAfterPromo * 10));
  const pointsDiscountValue = redeem && canRedeem ? maxRedeemablePoints / 10 : 0;

  const finalTotal = total() - pointsDiscountValue;
  const delivery = finalTotal > 25 ? 0 : 4.99;

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
        totalAmount: finalTotal + delivery,
        pointsRedeemed: redeem ? maxRedeemablePoints : 0,
        discountAmount: couponDiscount + pointsDiscountValue,
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
                <div className="flex flex-col gap-0.5">
                  <p className="text-sm text-muted-foreground">₹{i.price.toFixed(2)} each</p>
                  {i.bundleItems && (
                    <p className="text-[10px] text-emerald-600 font-medium italic">
                      Includes: {i.bundleItems.split(",").join(" + ")}
                    </p>
                  )}
                </div>
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
          {appliedBundles.length > 0 && (
            <div className="mb-4 rounded-xl bg-emerald-50 border border-emerald-100 p-3 animate-in fade-in slide-in-from-top-1 duration-500">
              <p className="text-[11px] font-bold text-emerald-700 uppercase tracking-wider mb-1">🎉 Bundle Offer Applied!</p>
              <p className="text-xs text-emerald-600 font-medium leading-tight">
                You saved <span className="font-bold text-emerald-700">₹{bundleDiscount.toFixed(2)}</span> by grouping these items into a Health Package.
              </p>
            </div>
          )}
          <h2 className="text-lg font-semibold">Order summary</h2>
          <dl className="mt-4 space-y-2 text-sm">
            <div className="flex justify-between"><dt className="text-muted-foreground">Items Total</dt><dd>₹{itemsTotal.toFixed(2)}</dd></div>
            
            {appliedBundles.length > 0 && (
              <div className="space-y-1">
                {appliedBundles.map(b => (
                  <div key={b.name} className="flex justify-between text-emerald-600 font-medium italic">
                    <dt className="flex items-center gap-1">
                      <Coins className="h-3 w-3" /> {b.name} Discount
                    </dt>
                    <dd>-₹{b.discount.toFixed(2)}</dd>
                  </div>
                ))}
              </div>
            )}

            <div className="flex justify-between"><dt className="text-muted-foreground">Delivery</dt><dd>{delivery === 0 ? "Free" : `₹${delivery.toFixed(2)}`}</dd></div>
            
            <div className="mt-4 space-y-2">
               <div className="flex gap-2">
                 <Input 
                   placeholder="Coupon code" 
                   value={couponCode} 
                   onChange={e => setCouponCode(e.target.value)}
                   className="h-9 text-xs rounded-xl"
                 />
                 <Button size="sm" variant="secondary" onClick={handleApplyCoupon} className="h-9 px-3 rounded-xl text-xs">Apply</Button>
               </div>
               
               {appliedCoupon && (
                 <div className="flex justify-between items-center text-xs text-blue-600 font-medium bg-blue-50/50 border border-blue-100/50 p-2 rounded-xl">
                    <span className="flex items-center gap-1"><Ticket className="h-3 w-3" /> {appliedCoupon.code} ({appliedCoupon.discount}%)</span>
                    <span>-₹{couponDiscount.toFixed(2)}</span>
                 </div>
               )}
            </div>

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
                    ✓ Using {maxRedeemablePoints} pts → ₹{pointsDiscountValue.toFixed(2)} discount applied
                  </div>
                )}

                <p className="mt-2 text-[10px] text-muted-foreground/70 border-t border-border/30 pt-2">
                  You'll earn <span className="font-semibold text-foreground">{Math.floor((subtotalAfterPromo - pointsDiscountValue) / 100)}</span> points from this order
                </p>
              </div>
            )}

            {pointsDiscountValue > 0 && (
              <div className="flex justify-between text-success-fg"><dt>Points Discount</dt><dd>-₹{pointsDiscountValue.toFixed(2)}</dd></div>
            )}

            <div className="my-3 border-t border-border" />
            <div className="flex justify-between text-base font-bold"><dt>Total</dt><dd>₹{(finalTotal + delivery).toFixed(2)}</dd></div>
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
