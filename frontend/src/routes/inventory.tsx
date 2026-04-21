import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { Boxes, Search, CheckCircle2, AlertTriangle, XCircle } from "lucide-react";
import { api, type Inventory } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/inventory")({
  head: () => ({ meta: [{ title: "Stock — VerdeRx" }] }),
  component: InventoryPage,
});

function InventoryPage() {
  const [id, setId] = useState("");
  const [data, setData] = useState<Inventory | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const lookup = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!id) return;
    setLoading(true); setError(null); setData(null);
    try {
      const res = await api.get<Inventory>(`/inventory/${id}`);
      setData(res.data);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  const statusMeta = (s?: string) => {
    const v = (s ?? "").toUpperCase();
    if (v.includes("OUT")) return { Icon: XCircle, color: "text-destructive", bg: "bg-destructive/10" };
    if (v.includes("LOW")) return { Icon: AlertTriangle, color: "text-accent", bg: "bg-accent-soft" };
    return { Icon: CheckCircle2, color: "text-primary", bg: "bg-primary/10" };
  };

  return (
    <div className="mx-auto max-w-3xl px-4 py-12 md:px-8">
      <div className="text-center">
        <span className="inline-flex h-12 w-12 items-center justify-center rounded-2xl bg-[image:var(--gradient-primary)] text-primary-foreground">
          <Boxes className="h-6 w-6" />
        </span>
        <h1 className="mt-4 text-3xl font-bold tracking-tight md:text-4xl">Live stock lookup</h1>
        <p className="mt-2 text-muted-foreground">Check real-time availability for any product.</p>
      </div>

      <form onSubmit={lookup} className="mt-8 flex gap-2 rounded-full border border-border/60 bg-card p-2 shadow-[var(--shadow-card)]">
        <div className="relative flex-1">
          <Search className="absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={id}
            onChange={(e) => setId(e.target.value)}
            placeholder="Enter product ID (e.g. 1)"
            className="border-0 bg-transparent pl-10 shadow-none focus-visible:ring-0"
          />
        </div>
        <Button type="submit" disabled={loading || !id} className="rounded-full px-6">
          {loading ? "Checking…" : "Check"}
        </Button>
      </form>

      {error && (
        <div className="mt-6 rounded-2xl border border-destructive/40 bg-destructive/5 p-4 text-sm text-destructive">
          {error}
        </div>
      )}

      {data && (() => {
        const { Icon, color, bg } = statusMeta(data.status);
        return (
          <div className="mt-8 rounded-3xl border border-border/60 bg-card p-8 shadow-[var(--shadow-soft)]">
            <div className="flex items-center gap-4">
              <span className={`flex h-14 w-14 items-center justify-center rounded-2xl ${bg} ${color}`}>
                <Icon className="h-7 w-7" />
              </span>
              <div>
                <p className="text-sm text-muted-foreground">Product ID</p>
                <p className="text-xl font-bold">#{data.productId}</p>
              </div>
            </div>
            <div className="mt-6 grid gap-4 sm:grid-cols-2">
              <div className="rounded-2xl bg-secondary/50 p-4">
                <p className="text-xs uppercase text-muted-foreground">Quantity</p>
                <p className="mt-1 text-2xl font-bold">{data.quantity}</p>
              </div>
              <div className="rounded-2xl bg-secondary/50 p-4">
                <p className="text-xs uppercase text-muted-foreground">Status</p>
                <p className={`mt-1 text-2xl font-bold ${color}`}>{data.status}</p>
              </div>
            </div>
          </div>
        );
      })()}
    </div>
  );
}
