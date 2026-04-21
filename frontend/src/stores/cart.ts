import { create } from "zustand";
import { persist } from "zustand/middleware";
import type { Product } from "@/lib/api";

export type CartItem = {
  productId: string | number;
  name: string;
  price: number;
  quantity: number;
};

type CartState = {
  items: CartItem[];
  add: (p: Product, qty?: number) => void;
  remove: (id: string | number) => void;
  setQty: (id: string | number, qty: number) => void;
  clear: () => void;
  total: () => number;
  count: () => number;
};

export const useCart = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],
      add: (p, qty = 1) =>
        set((s) => {
          const ex = s.items.find((i) => i.productId === p.id);
          if (ex) {
            return {
              items: s.items.map((i) =>
                i.productId === p.id ? { ...i, quantity: i.quantity + qty } : i,
              ),
            };
          }
          return {
            items: [...s.items, { productId: p.id, name: p.name, price: p.price, quantity: qty }],
          };
        }),
      remove: (id) => set((s) => ({ items: s.items.filter((i) => i.productId !== id) })),
      setQty: (id, qty) =>
        set((s) => ({
          items: s.items
            .map((i) => (i.productId === id ? { ...i, quantity: Math.max(1, qty) } : i))
            .filter((i) => i.quantity > 0),
        })),
      clear: () => set({ items: [] }),
      total: () => get().items.reduce((a, i) => a + i.price * i.quantity, 0),
      count: () => get().items.reduce((a, i) => a + i.quantity, 0),
    }),
    { name: "pharma_cart" },
  ),
);
