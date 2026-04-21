import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { ArrowLeft, ShoppingCart, FileText, Leaf, ShieldCheck, Truck } from "lucide-react";
import { api, type Product } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { useCart } from "@/stores/cart";
import { PrescriptionUpload } from "@/components/PrescriptionUpload";
import { toast } from "sonner";

export const Route = createFileRoute("/products/$id")({
  component: ProductDetail,
});

function ProductDetail() {
  const { id } = Route.useParams();
  const add = useCart((s) => s.add);
  const prescriptionId = useCart((s) => s.prescriptionId);
  const setPrescription = useCart((s) => s.setPrescription);

  const { data: p, isLoading } = useQuery({
    queryKey: ["product", id],
    queryFn: async () => (await api.get<Product>(`/products/${id}`)).data,
    retry: false,
  });

  if (isLoading) {
    return <div className="mx-auto max-w-7xl px-4 py-20 text-center">Loading product...</div>;
  }

  if (!p) {
    return <div className="mx-auto max-w-7xl px-4 py-20 text-center">Product not found.</div>;
  }

  return (
    <div className="mx-auto max-w-6xl px-4 py-10 md:px-8">
      <Link to="/catalog" className="inline-flex items-center gap-1 text-sm text-muted-foreground hover:text-foreground">
        <ArrowLeft className="h-4 w-4" /> Back to catalog
      </Link>

      <div className="mt-6 grid gap-10 md:grid-cols-2">
        <div className="relative overflow-hidden rounded-3xl bg-[image:var(--gradient-hero)] p-10">
          <div className="flex aspect-square items-center justify-center">
            <Leaf className="h-40 w-40 text-primary/70" />
          </div>
        </div>
        <div className="flex flex-col">
          <div className="flex items-center justify-between">
            <p className="text-sm uppercase tracking-wide text-primary">{p.category?.name ?? "General"}</p>
            <span className="text-xs font-mono text-muted-foreground bg-secondary px-2 py-0.5 rounded-md">ID: {p.id}</span>
          </div>
          <h1 className="mt-2 text-3xl font-bold tracking-tight md:text-4xl">{p.name}</h1>
          {p.prescriptionRequired && (
            <span className="mt-3 inline-flex w-fit items-center gap-1 rounded-full bg-accent-soft px-3 py-1 text-xs font-medium text-accent">
              <FileText className="h-3.5 w-3.5" /> Prescription required
            </span>
          )}
          <p className="mt-4 text-muted-foreground">{p.description ?? "Trusted, lab-tested formulation from PharmaOrder."}</p>
          <p className="mt-6 text-4xl font-bold">₹{p.price?.toFixed(2)}</p>
          <p className="mt-1 text-sm text-muted-foreground">In Stock: {p.quantity ?? 0} units</p>
          
          {(p.dosage || p.packaging) && (
            <div className="mt-5 flex flex-wrap gap-3">
              {p.dosage && (
                <div className="flex flex-col rounded-2xl bg-secondary/30 px-4 py-2">
                  <span className="text-[10px] uppercase tracking-wider text-muted-foreground">Dosage</span>
                  <span className="text-sm font-semibold">{p.dosage}</span>
                </div>
              )}
              {p.packaging && (
                <div className="flex flex-col rounded-2xl bg-secondary/30 px-4 py-2">
                  <span className="text-[10px] uppercase tracking-wider text-muted-foreground">Packaging</span>
                  <span className="text-sm font-semibold">{p.packaging}</span>
                </div>
              )}
            </div>
          )}

          {p.prescriptionRequired && (
            <div className="mt-8">
              <PrescriptionUpload 
                onSuccess={(id) => {
                  useCart.getState().setPrescription(id);
                }} 
              />
            </div>
          )}

          <div className="mt-8 flex gap-3">
            <Button
              size="lg"
              disabled={p.prescriptionRequired && !prescriptionId}
              className="flex-1 rounded-full shadow-[var(--shadow-glow)]"
              onClick={() => { add(p); toast.success(`${p.name} added to cart`); }}
            >
              <ShoppingCart className="mr-2 h-5 w-5" /> 
              {p.prescriptionRequired && !prescriptionId ? "Upload required" : "Add to cart"}
            </Button>
            <Link to="/cart" className="flex-1">
              <Button size="lg" variant="outline" className="w-full rounded-full">View cart</Button>
            </Link>
          </div>

          <div className="mt-8 grid gap-3 rounded-2xl border border-border/60 bg-card p-5">
            <div className="flex items-center gap-3 text-sm"><ShieldCheck className="h-5 w-5 text-primary" /> Authentic & verified</div>
            <div className="flex items-center gap-3 text-sm"><Truck className="h-5 w-5 text-primary" /> Free same-day delivery over ₹250</div>
            <div className="flex items-center gap-3 text-sm"><Leaf className="h-5 w-5 text-primary" /> Earn {Math.floor((p.price ?? 0) / 100)} health points on this item</div>
          </div>
        </div>
      </div>
    </div>
  );
}
