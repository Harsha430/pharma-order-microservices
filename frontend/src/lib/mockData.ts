import type { Product, Category, Order, Inventory, Profile } from "@/lib/api";

export const mockCategories: Category[] = [
  { id: 1, name: "Pain Relief", description: "Headache, fever, muscle relief" },
  { id: 2, name: "Vitamins", description: "Daily essentials & supplements" },
  { id: 3, name: "Cold & Flu", description: "Cough, congestion, sore throat" },
  { id: 4, name: "Skin Care", description: "Dermatologist-grade essentials" },
  { id: 5, name: "Digestive", description: "Gut, acidity, probiotics" },
  { id: 6, name: "Heart & BP", description: "Cardio wellness" },
];

export const mockProducts: Product[] = [
  { id: 1, name: "Paracetamol 500mg", price: 4.99, category: { name: "Pain Relief" }, status: "ACTIVE", prescriptionRequired: false, description: "Fast acting fever and pain reducer." },
  { id: 2, name: "Vitamin D3 2000 IU", price: 12.5, category: { name: "Vitamins" }, status: "ACTIVE", description: "Daily immunity & bone strength support." },
  { id: 3, name: "Cough Syrup Honey-Lime", price: 8.75, category: { name: "Cold & Flu" }, status: "ACTIVE", description: "Soothing relief from dry cough." },
  { id: 4, name: "Hyaluronic Acid Serum", price: 24.0, category: { name: "Skin Care" }, status: "ACTIVE", description: "Deep hydration for radiant skin." },
  { id: 5, name: "Probiotic Complex", price: 18.9, category: { name: "Digestive" }, status: "ACTIVE", description: "10 billion CFU for gut health." },
  { id: 6, name: "Amoxicillin 250mg", price: 14.2, category: { name: "Cold & Flu" }, prescriptionRequired: true, status: "ACTIVE", description: "Antibiotic — prescription required." },
  { id: 7, name: "Omega-3 Fish Oil", price: 19.5, category: { name: "Heart & BP" }, status: "ACTIVE", description: "Heart-healthy EPA + DHA." },
  { id: 8, name: "Multivitamin Daily", price: 15.0, category: { name: "Vitamins" }, status: "ACTIVE", description: "26 essential nutrients in one capsule." },
];

export const mockOrders: Order[] = [
  {
    id: "ORD-1042",
    totalAmount: 47.24,
    status: "DELIVERED",
    createdAt: "2026-04-15",
    orderItems: [
      { productId: 2, name: "Vitamin D3 2000 IU", price: 12.5, quantity: 2 },
      { productId: 4, name: "Hyaluronic Acid Serum", price: 24.0, quantity: 1 },
    ],
  },
  {
    id: "ORD-1051",
    totalAmount: 18.9,
    status: "SHIPPED",
    createdAt: "2026-04-18",
    orderItems: [{ productId: 5, name: "Probiotic Complex", price: 18.9, quantity: 1 }],
  },
  {
    id: "ORD-1063",
    totalAmount: 9.98,
    status: "PROCESSING",
    createdAt: "2026-04-20",
    orderItems: [{ productId: 1, name: "Paracetamol 500mg", price: 4.99, quantity: 2 }],
  },
];

export const mockInventory: Record<string, Inventory> = {
  "1": { productId: 1, quantity: 248, status: "IN_STOCK" },
  "2": { productId: 2, quantity: 56, status: "IN_STOCK" },
  "3": { productId: 3, quantity: 12, status: "LOW_STOCK" },
  "4": { productId: 4, quantity: 0, status: "OUT_OF_STOCK" },
  "5": { productId: 5, quantity: 87, status: "IN_STOCK" },
  "6": { productId: 6, quantity: 8, status: "LOW_STOCK" },
  "7": { productId: 7, quantity: 134, status: "IN_STOCK" },
  "8": { productId: 8, quantity: 41, status: "IN_STOCK" },
};

export const mockProfile: Profile = {
  id: "u_001",
  name: "Alex Morgan",
  email: "alex@verderx.demo",
  phone: "+1 (555) 010-2024",
  roles: ["USER"],
  healthPoints: 285,
};
