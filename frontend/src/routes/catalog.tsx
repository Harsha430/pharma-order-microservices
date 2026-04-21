import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import { useMemo, useState } from "react";
import { Search, FilterX } from "lucide-react";
import { api, type Product } from "@/lib/api";
import { mockProducts, mockCategories } from "@/lib/mockData";
import { ProductCard } from "@/components/ProductCard";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

type CatalogSearch = { category?: string };

export const Route = createFileRoute("/catalog")({
  validateSearch: (s: Record<string, unknown>): CatalogSearch => ({
    category: typeof s.category === "string" ? s.category : undefined,
  }),
  head: () => ({
    meta: [
      { title: "Catalog — VerdeRx" },
      { name: "description", content: "Browse vitamins, prescriptions, skincare and more from VerdeRx pharmacy." },
    ],
  }),
  component: CatalogPage,
});

function CatalogPage() {
  const { category } = Route.useSearch();
  const nav = Route.useNavigate();
  const [q, setQ] = useState("");

  const { data: products = [] } = useQuery({
    queryKey: ["products"],
    queryFn: async () => (await api.get<Product[]>("/products")).data,
    retry: false,
  });

  const { data: categories = [] } = useQuery({
    queryKey: ["categories"],
    queryFn: async () => (await api.get<Category[]>("/products/categories")).data,
    retry: false,
  });

  const filtered = useMemo(() => {
    return products.filter((p) => {
      const matchCat = !category || p.category?.name === category;
      const matchQ = !q || p.name.toLowerCase().includes(q.toLowerCase());
      return matchCat && matchQ;
    });
  }, [products, category, q]);

  return (
    <div className="mx-auto max-w-7xl px-4 py-12 md:px-8">
      <div className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight md:text-4xl">Catalog</h1>
          <p className="mt-1 text-muted-foreground">{filtered.length} products available</p>
        </div>
        <div className="relative w-full md:w-80">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search medicines, vitamins…"
            value={q}
            onChange={(e) => setQ(e.target.value)}
            className="rounded-full pl-9"
          />
        </div>
      </div>

      <div className="mt-6 flex flex-wrap items-center gap-2">
        <Link
          to="/catalog"
          className={`rounded-full border px-4 py-1.5 text-sm transition ${!category ? "border-primary bg-primary text-primary-foreground" : "border-border hover:bg-secondary"}`}
        >
          All
        </Link>
        {categories.map((c) => (
          <Link
            key={c.id}
            to="/catalog"
            search={{ category: c.name }}
            className={`rounded-full border px-4 py-1.5 text-sm transition ${category === c.name ? "border-primary bg-primary text-primary-foreground" : "border-border hover:bg-secondary"}`}
          >
            {c.name}
          </Link>
        ))}
        {(category || q) && (
          <Button variant="ghost" size="sm" onClick={() => { setQ(""); nav({ to: "/catalog", search: {} }); }}>
            <FilterX className="mr-1 h-4 w-4" /> Clear
          </Button>
        )}
      </div>

      <div className="mt-8 grid gap-5 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
        {filtered.map((p) => <ProductCard key={p.id} product={p} />)}
      </div>

      {filtered.length === 0 && (
        <div className="mt-16 text-center text-muted-foreground">No products match your search.</div>
      )}
    </div>
  );
}
