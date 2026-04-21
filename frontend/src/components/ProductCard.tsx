import { Link } from "@tanstack/react-router";
import { ShoppingCart, FileText, Leaf } from "lucide-react";
import type { Product } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { useCart } from "@/stores/cart";
import { toast } from "sonner";

export function ProductCard({ product }: { product: Product }) {
  const add = useCart((s) => s.add);
  const rx = product.prescriptionRequired;

  return (
    <div className="group relative overflow-hidden rounded-2xl border border-border/60 bg-card p-5 shadow-[var(--shadow-card)] transition-[transform,box-shadow] duration-300 hover:-translate-y-1 hover:shadow-[var(--shadow-soft)]">
      <div className="flex aspect-square items-center justify-center rounded-xl bg-[image:var(--gradient-hero)]">
        <Leaf className="h-14 w-14 text-primary/70 transition-transform duration-500 group-hover:scale-110" />
      </div>
      <div className="mt-4 flex items-start justify-between gap-2">
        <div>
          <p className="text-xs uppercase tracking-wide text-muted-foreground">
            {product.category?.name ?? "General"}
          </p>
          <Link to="/products/$id" params={{ id: String(product.id) }} className="mt-1 line-clamp-2 block font-semibold leading-tight hover:text-primary">
            {product.name}
          </Link>
        </div>
        {rx && (
          <span className="flex shrink-0 items-center gap-1 rounded-full bg-accent-soft px-2 py-0.5 text-[10px] font-medium text-accent">
            <FileText className="h-3 w-3" /> Rx
          </span>
        )}
      </div>
      <div className="mt-4 flex items-center justify-between">
        <span className="text-lg font-bold text-foreground">${product.price?.toFixed(2)}</span>
        <Button
          size="sm"
          className="rounded-full"
          onClick={() => { add(product); toast.success(`${product.name} added to cart`); }}
        >
          <ShoppingCart className="mr-1 h-4 w-4" /> Add
        </Button>
      </div>
    </div>
  );
}
