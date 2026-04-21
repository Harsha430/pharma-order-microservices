import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { Product } from "@/lib/api";

export type CartItem = {
  productId: string | number;
  name: string;
  price: number;
  quantity: number;
  prescriptionRequired?: boolean;
  bundleItems?: string;
};

export type AppliedBundle = {
  id: string | number;
  name: string;
  discount: number;
};

type CartState = {
  items: CartItem[];
  prescriptionId: string | null;
  appliedBundles: AppliedBundle[];
  appliedCoupon: { code: string; discount: number } | null;
  add: (p: Product, qty?: number) => void;
  addMultiple: (products: Product[]) => void;
  remove: (id: string | number) => void;
  setQty: (id: string | number, qty: number) => void;
  setPrescription: (id: string | null) => void;
  applyCoupon: (code: string) => boolean;
  clear: () => void;
  total: () => number;
  count: () => number;
  refreshBundles: () => void;
};

// Coupon Registry
const COUPONS: Record<string, number> = {
  "SAVER20": 20,
  "HEALTH10": 10,
  "FIRST50": 50,
  "WELCOME5": 5
};

// Bundle Definitions (Hardcoded for demo consistency with DataSeeder)
const BUNDLES = [
  {
    name: "Post-Fever Recovery Pack",
    price: 299,
    items: ["Dolo 650 Tablets", "Zincovit", "ORS Powder", "Multivitamins"]
  },
  {
    name: "Immunity Booster Bundle",
    price: 599,
    items: ["Chyawanprash 500g", "Vitamin C 1000mg", "Ashvagandha", "Tulsi Drops"]
  },
  {
    name: "Cardiac Essentials Kit",
    price: 1299,
    items: ["BP Monitor", "Ecosprin 75", "Fish Oil Capsules"]
  }
];

export const useCart = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],
      prescriptionId: null,
      appliedBundles: [],
      appliedCoupon: null,
      add: (p, qty = 1) => {
        set((s) => {
          const ex = s.items.find((i) => i.productId === p.id);
          let newItems = [];
          if (ex) {
            newItems = s.items.map((i) =>
              i.productId === p.id 
                ? { ...i, quantity: i.quantity + qty, prescriptionRequired: p.prescriptionRequired, bundleItems: p.bundleItems } 
                : i,
            );
          } else {
            newItems = [...s.items, { 
              productId: p.id, 
              name: p.name, 
              price: p.price, 
              quantity: qty,
              prescriptionRequired: p.prescriptionRequired,
              bundleItems: p.bundleItems
            }];
          }
          return { items: newItems };
        });
        get().refreshBundles();
      },
      addMultiple: (products) => {
        products.forEach(p => get().add(p, 1));
      },
      applyCoupon: (code) => {
        const c = code.toUpperCase();
        if (COUPONS[c]) {
          set({ appliedCoupon: { code: c, discount: COUPONS[c] } });
          return true;
        }
        return false;
      },
      remove: (id) => {
        set((s) => ({ items: s.items.filter((i) => i.productId !== id) }));
        get().refreshBundles();
      },
      setQty: (id, qty) => {
        set((s) => ({
          items: s.items
            .map((i) => (i.productId === id ? { ...i, quantity: Math.max(1, qty) } : i))
            .filter((i) => i.quantity > 0),
        }));
        get().refreshBundles();
      },
      refreshBundles: () => {
        const { items } = get();
        const applied: AppliedBundle[] = [];
        
        BUNDLES.forEach(b => {
          const matchingItems = items.filter(i => b.items.includes(i.name));
          // If all components are present (at least 1 of each)
          if (matchingItems.length === b.items.length) {
            const individualTotal = matchingItems.reduce((a, i) => a + i.price, 0);
            const discount = individualTotal - b.price;
            if (discount > 0) {
              applied.push({ id: b.name, name: b.name, discount });
            }
          }
        });
        
        set({ appliedBundles: applied });
      },
      setPrescription: (id) => set({ prescriptionId: id }),
      clear: () => set({ items: [], prescriptionId: null, appliedBundles: [], appliedCoupon: null }),
      total: () => {
        const itemTotal = get().items.reduce((a, i) => a + i.price * i.quantity, 0);
        const bundleDiscount = get().appliedBundles.reduce((a, b) => a + b.discount, 0);
        const subtotal = itemTotal - bundleDiscount;
        
        const coupon = get().appliedCoupon;
        const couponDiscount = coupon ? (subtotal * (coupon.discount / 100)) : 0;
        
        return Math.max(0, subtotal - couponDiscount);
      },
      count: () => get().items.reduce((a, i) => a + i.quantity, 0),
    }),
    { name: "pharma_cart" },
  ),
);
