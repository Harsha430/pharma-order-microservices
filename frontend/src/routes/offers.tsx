import { createFileRoute } from "@tanstack/react-router";
import { Ticket, Copy, CheckCircle2, Zap, Star, Gift } from "lucide-react";
import { useState } from "react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/offers")({
  component: OffersPage,
});

const COUPONS = [
  {
    code: "SAVER20",
    title: "Flat 20% OFF",
    desc: "Get 20% discount on all orders above ₹1000.",
    type: "Storewide",
    color: "bg-blue-500",
  },
  {
    code: "HEALTH10",
    title: "10% Health Boost",
    desc: "Extra 10% off on all Health Packages and Bundles.",
    type: "Bundles Only",
    color: "bg-emerald-500",
  },
  {
    code: "FIRST50",
    title: "New User Special",
    desc: "Flat 50% discount on your very first order at PharmaOrder.",
    type: "First Order",
    color: "bg-purple-600",
  },
  {
    code: "WELCOME5",
    title: "Welcome Coupon",
    desc: "Small gift for our new community members.",
    type: "Storewide",
    color: "bg-amber-500",
  },
];

function OffersPage() {
  const [copied, setCopied] = useState<string | null>(null);

  const copy = (code: string) => {
    navigator.clipboard.writeText(code);
    setCopied(code);
    toast.success(`Coupon ${code} copied!`);
    setTimeout(() => setCopied(null), 2000);
  };

  return (
    <div className="mx-auto max-w-7xl px-4 py-12 md:px-8">
      <div className="text-center mb-16">
        <h1 className="text-4xl font-black tracking-tight md:text-5xl mb-4 bg-gradient-to-r from-emerald-600 to-teal-500 bg-clip-text text-transparent">
          Exclusive Offers & Coupons
        </h1>
        <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
          Maximize your wellness savings with our curated discount codes. 
          Simply copy and apply during checkout.
        </p>
      </div>

      <div className="grid gap-8 md:grid-cols-2">
        {COUPONS.map((c) => (
          <div 
            key={c.code} 
            className="group relative overflow-hidden rounded-3xl border border-border/60 bg-card p-8 shadow-[var(--shadow-card)] transition-all hover:shadow-[var(--shadow-soft)]"
          >
            <div className={`absolute top-0 right-0 w-32 h-32 ${c.color} opacity-5 rounded-full -translate-y-1/2 translate-x-1/2 blur-2xl group-hover:scale-150 transition-transform duration-700`} />
            
            <div className="relative flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-3">
                  <span className={`px-2 py-0.5 rounded text-[10px] font-bold text-white uppercase tracking-wider ${c.color}`}>
                    {c.type}
                  </span>
                </div>
                <h3 className="text-2xl font-bold mb-2">{c.title}</h3>
                <p className="text-muted-foreground leading-relaxed">
                  {c.desc}
                </p>
              </div>
              
              <div className="w-full md:w-auto shrink-0">
                <div className="flex flex-col items-center gap-3 p-4 bg-secondary/30 rounded-2xl border border-dashed border-border/60">
                   <span className="text-xl font-black tracking-widest text-foreground">{c.code}</span>
                   <Button 
                    variant={copied === c.code ? "secondary" : "default"}
                    size="sm" 
                    className="w-full rounded-xl gap-2 h-10"
                    onClick={() => copy(c.code)}
                   >
                     {copied === c.code ? (
                       <><CheckCircle2 className="h-4 w-4" /> Copied</>
                     ) : (
                       <><Copy className="h-4 w-4" /> Copy Code</>
                     )}
                   </Button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="mt-20 rounded-[40px] bg-gradient-to-br from-secondary/50 via-background to-secondary/30 border border-border/60 p-10 md:p-16 relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-emerald-500 via-teal-400 to-blue-500" />
        
        <div className="grid md:grid-cols-3 gap-12 text-center md:text-left">
          <div className="space-y-4">
             <div className="w-12 h-12 rounded-2xl bg-emerald-100 flex items-center justify-center text-emerald-600 mb-2">
                <Zap className="h-6 w-6" />
             </div>
             <h4 className="text-xl font-bold">Instant Savings</h4>
             <p className="text-muted-foreground">Coupons are applied instantly to your cart total, including taxes.</p>
          </div>
          <div className="space-y-4">
             <div className="w-12 h-12 rounded-2xl bg-amber-100 flex items-center justify-center text-amber-600 mb-2">
                <Star className="h-6 w-6" />
             </div>
             <h4 className="text-xl font-bold">Stackable Rewards</h4>
             <p className="text-muted-foreground">Use coupons alongside your Health Points for maximum possible discounts.</p>
          </div>
          <div className="space-y-4">
             <div className="w-12 h-12 rounded-2xl bg-purple-100 flex items-center justify-center text-purple-600 mb-2">
                <Gift className="h-6 w-6" />
             </div>
             <h4 className="text-xl font-bold">New Offers Weekly</h4>
             <p className="text-muted-foreground">Check back every Monday for fresh seasonal and wellness coupons.</p>
          </div>
        </div>
      </div>
    </div>
  );
}
