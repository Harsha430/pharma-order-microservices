import { Pill } from "lucide-react";

export function Footer() {
  return (
    <footer className="mt-24 border-t border-border/60 bg-secondary/40">
      <div className="mx-auto grid max-w-7xl gap-8 px-4 py-12 md:grid-cols-4 md:px-8">
        <div>
          <div className="flex items-center gap-2">
            <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-[image:var(--gradient-primary)] text-primary-foreground">
              <Pill className="h-5 w-5" />
            </span>
            <span className="text-lg font-semibold">VerdeRx</span>
          </div>
          <p className="mt-3 text-sm text-muted-foreground">
            Wellness, delivered with care. Licensed pharmacy you can trust.
          </p>
        </div>
        <div>
          <h4 className="text-sm font-semibold">Shop</h4>
          <ul className="mt-3 space-y-2 text-sm text-muted-foreground">
            <li>Catalog</li><li>Categories</li><li>Stock</li>
          </ul>
        </div>
        <div>
          <h4 className="text-sm font-semibold">Care</h4>
          <ul className="mt-3 space-y-2 text-sm text-muted-foreground">
            <li>Pharmacist chat</li><li>Prescriptions</li><li>Refills</li>
          </ul>
        </div>
        <div>
          <h4 className="text-sm font-semibold">Trust</h4>
          <ul className="mt-3 space-y-2 text-sm text-muted-foreground">
            <li>Privacy</li><li>Terms</li><li>Licenses</li>
          </ul>
        </div>
      </div>
      <div className="border-t border-border/60 py-4 text-center text-xs text-muted-foreground">
        © {new Date().getFullYear()} VerdeRx Pharmacy. All rights reserved.
      </div>
    </footer>
  );
}
