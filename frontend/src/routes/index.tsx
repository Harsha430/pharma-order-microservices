import { createFileRoute, Link } from "@tanstack/react-router";
import { ArrowRight, ShieldCheck, Truck, HeartPulse, Sparkles, Leaf, Pill } from "lucide-react";
import { Button } from "@/components/ui/button";
import { ProductCard } from "@/components/ProductCard";
import PromoBanner from "@/components/PromoBanner";
import { api, type Product, type Category } from "@/lib/api";
import { useQuery } from "@tanstack/react-query";
import heroImg from "@/assets/hero.jpg";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "PharmaOrder — Wellness, delivered with care" },
      { name: "description", content: "Order medicines, vitamins and skincare from a trusted modern pharmacy. Fast delivery, licensed pharmacists, health rewards." },
      { property: "og:title", content: "PharmaOrder — Wellness, delivered with care" },
      { property: "og:description", content: "Premium online pharmacy. Delivered fast, with care." },
    ],
  }),
  component: HomePage,
});

function HomePage() {
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

  const featured = products.slice(0, 4);

  return (
    <div>
      {/* HERO */}
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 bg-[image:var(--gradient-hero)]" />
        <div className="relative mx-auto grid max-w-7xl gap-10 px-4 py-16 md:grid-cols-2 md:px-8 md:py-24">
          <div className="flex flex-col justify-center">
            <span className="inline-flex w-fit items-center gap-2 rounded-full border border-primary/20 bg-background/60 px-3 py-1 text-xs font-medium text-primary backdrop-blur">
              <Sparkles className="h-3.5 w-3.5" /> Licensed pharmacists on call 24/7
            </span>
            <h1 className="mt-5 text-4xl font-bold leading-[1.05] tracking-tight md:text-6xl">
              Wellness, <span className="bg-[image:var(--gradient-primary)] bg-clip-text text-transparent">delivered with care.</span>
            </h1>
            <p className="mt-5 max-w-lg text-base text-muted-foreground md:text-lg">
              From daily vitamins to specialty prescriptions — PharmaOrder makes feeling
              your best feel effortless.
            </p>
            <div className="mt-8 flex flex-wrap gap-3">
              <Link to="/catalog">
                <Button size="lg" className="rounded-full px-6 shadow-[var(--shadow-glow)]">
                  Shop now <ArrowRight className="ml-1 h-4 w-4" />
                </Button>
              </Link>
              <Link to="/inventory">
                <Button size="lg" variant="outline" className="rounded-full px-6">Check stock</Button>
              </Link>
            </div>
            <div className="mt-10 grid grid-cols-3 gap-4 text-center md:max-w-md">
              {[
                { v: "150+", l: "Health points" },
                { v: "24/7", l: "Pharmacist" },
                { v: "<1hr", l: "Delivery" },
              ].map((s) => (
                <div key={s.l} className="rounded-xl border border-border/60 bg-background/60 px-3 py-3 backdrop-blur">
                  <p className="text-xl font-bold text-primary">{s.v}</p>
                  <p className="text-xs text-muted-foreground">{s.l}</p>
                </div>
              ))}
            </div>
          </div>
          <div className="relative">
            <div className="absolute -inset-4 rounded-[2rem] bg-[image:var(--gradient-primary)] opacity-20 blur-2xl" />
            <img
              src={heroImg}
              alt="Modern wellness pharmacy with leaves, capsules and stethoscope"
              width={1600}
              height={1100}
              className="relative aspect-[4/3] w-full rounded-[2rem] object-cover shadow-[var(--shadow-glow)]"
            />
          </div>
        </div>
      </section>

      {/* TRUST BAR */}
      <section className="border-y border-border/60 bg-card">
        <div className="mx-auto grid max-w-7xl grid-cols-2 gap-6 px-4 py-8 md:grid-cols-4 md:px-8">
          {[
            { i: ShieldCheck, t: "Licensed & verified" },
            { i: Truck, t: "Free same-day delivery" },
            { i: HeartPulse, t: "Earn health points" },
            { i: Leaf, t: "Authentic medication" },
          ].map(({ i: Icon, t }) => (
            <div key={t} className="flex items-center gap-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-secondary text-primary"><Icon className="h-5 w-5" /></span>
              <p className="text-sm font-medium">{t}</p>
            </div>
          ))}
        </div>
      </section>

      {/* PROMO BANNER */}
      <section className="mx-auto max-w-7xl px-4 pt-16 md:px-8">
        <PromoBanner />
      </section>

      {/* CATEGORIES */}
      <section className="mx-auto max-w-7xl px-4 py-16 md:px-8">
        <div className="flex items-end justify-between">
          <div>
            <h2 className="text-3xl font-bold tracking-tight md:text-4xl">Shop by category</h2>
            <p className="mt-2 text-muted-foreground">Find exactly what your body needs.</p>
          </div>
          <Link to="/catalog" className="hidden text-sm font-medium text-primary hover:underline md:inline">View all →</Link>
        </div>
        <div className="mt-8 grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-6">
          {categories.map((c, idx) => (
            <Link
              key={c.id}
              to="/catalog"
              search={{ category: c.name }}
              className="group flex flex-col items-center gap-3 rounded-2xl border border-border/60 bg-card p-5 text-center transition-all hover:-translate-y-1 hover:border-primary/40 hover:shadow-[var(--shadow-card)]"
            >
              <span
                className="flex h-12 w-12 items-center justify-center rounded-xl text-primary-foreground transition-transform group-hover:scale-110"
                style={{ background: idx % 2 ? "var(--gradient-accent)" : "var(--gradient-primary)" }}
              >
                <Pill className="h-6 w-6" />
              </span>
              <p className="text-sm font-semibold">{c.name}</p>
            </Link>
          ))}
        </div>
      </section>

      {/* FEATURED */}
      <section className="mx-auto max-w-7xl px-4 pb-20 md:px-8">
        <div className="flex items-end justify-between">
          <h2 className="text-3xl font-bold tracking-tight md:text-4xl">Featured today</h2>
          <Link to="/catalog" className="text-sm font-medium text-primary hover:underline">View all →</Link>
        </div>
        <div className="mt-8 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
          {featured.map((p) => <ProductCard key={p.id} product={p} />)}
        </div>
      </section>

      {/* CTA */}
      <section className="mx-auto max-w-7xl px-4 pb-20 md:px-8">
        <div className="relative overflow-hidden rounded-3xl bg-[image:var(--gradient-primary)] px-8 py-14 text-primary-foreground md:px-14">
          <div className="absolute -right-10 -top-10 h-48 w-48 rounded-full bg-accent/30 blur-3xl" />
          <div className="relative grid items-center gap-6 md:grid-cols-[1fr_auto]">
            <div>
              <h3 className="text-2xl font-bold md:text-3xl">Earn 150 health points on signup</h3>
              <p className="mt-2 max-w-xl text-primary-foreground/80">
                Track your wellness journey, redeem points on orders, and never miss a refill.
              </p>
            </div>
            <Link to="/register">
              <Button size="lg" variant="secondary" className="rounded-full px-6">
                Create account <ArrowRight className="ml-1 h-4 w-4" />
              </Button>
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
}
