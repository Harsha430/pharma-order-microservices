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
          <div className="flex items-center justify-between">
            <p className="text-xs uppercase tracking-wide text-muted-foreground">
              {product.category?.name ?? "General"}
            </p>
            <span className="text-[10px] font-mono text-muted-foreground/60">#ID:{product.id}</span>
          </div>
          <Link to="/products/$id" params={{ id: String(product.id) }} className="mt-1 line-clamp-2 block font-semibold leading-tight hover:text-primary">
            {product.name}
          </Link>
        </div>
        {rx && (
          <span className="flex shrink-0 items-center gap-1 rounded-full bg-accent-soft/20 px-2.5 py-0.5 text-[10px] font-bold text-accent">
            <FileText className="h-3 w-3" /> Rx Required
          </span>
        )}
      </div>
      {(product.dosage || product.packaging) && (
        <div className="mt-2 flex flex-wrap gap-2">
          {product.dosage && (
            <span className="rounded-md bg-secondary/40 px-1.5 py-0.5 text-[10px] font-medium text-secondary-foreground">
              {product.dosage}
            </span>
          )}
          {product.packaging && (
            <span className="rounded-md bg-secondary/40 px-1.5 py-0.5 text-[10px] font-medium text-secondary-foreground">
              {product.packaging}
            </span>
          )}
        </div>
      )}
      <div className="mt-4 flex items-center justify-between">
        <div className="flex flex-col">
          {product.originalPrice && product.originalPrice > product.price && (
            <span className="text-xs text-muted-foreground line-through decoration-destructive/40">
              ₹{product.originalPrice.toFixed(2)}
            </span>
          )}
          <span className="text-lg font-bold text-foreground">₹{product.price?.toFixed(2)}</span>
          <p className="text-[10px] text-muted-foreground mt-0.5">Stock: {product.quantity ?? 0}</p>
        </div>
        <Button
          size="sm"
          className="rounded-full"
          onClick={() => { add(product); toast.success(`${product.name} added to cart`); }}
          disabled={!product.quantity || product.quantity <= 0}
        >
          <ShoppingCart className="mr-1 h-4 w-4" /> {(!product.quantity || product.quantity <= 0) ? 'Out of Stock' : 'Add'}
        </Button>
      </div>
    </div>
  );
}
