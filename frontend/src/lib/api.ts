import axios from "axios";

const baseURL =
  (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? "http://localhost:8080";

export const api = axios.create({
  baseURL: `${baseURL}/api/v1`,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const token = window.localStorage.getItem("pharma_token");
    if (token) config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (r) => r,
  (error) => {
    const msg =
      error?.response?.data?.message || error?.message || "Something went wrong";
    return Promise.reject(new Error(msg));
  },
);

export const updateProfile = (data: Partial<Profile>) => api.put<Profile>("/users/me", data);

export type Category = { id: string | number; name: string; description?: string };
export type Product = {
  id: string | number;
  name: string;
  price: number;
  originalPrice?: number;
  description?: string;
  prescriptionRequired?: boolean;
  dosage?: string;
  packaging?: string;
  quantity?: number;
  status?: string;
  isBundle?: boolean;
  bundleItems?: string;
  category?: { name: string };
};
export type AuthResponse = { id: string; token: string; email: string; roles: string[] };
export type Profile = {
  id: string | number;
  email: string;
  firstName?: string;
  lastName?: string;
  phone?: string;
  address?: string;
  roles: string[];
  healthPoints?: number;
};
export type OrderItem = { productId: string | number; quantity: number; name?: string; price?: number };
export type Order = {
  id: string | number;
  totalAmount: number;
  pointsRedeemed?: number;
  discountAmount?: number;
  status: string;
  createdAt?: string;
  orderItems?: OrderItem[];
};
export type Inventory = { productId: string | number; quantity: number; status: string };
export type Loyalty = { userId: string; totalPoints: number };
