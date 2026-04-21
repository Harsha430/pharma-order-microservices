import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { useState, type FormEvent } from "react";
import { Mail, Lock, User, Phone, MapPin, Pill } from "lucide-react";
import { api, type AuthResponse } from "@/lib/api";
import { useAuth } from "@/stores/auth";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";

export const Route = createFileRoute("/register")({
  head: () => ({ meta: [{ title: "Create account — PharmaOrder" }] }),
  component: RegisterPage,
});

function RegisterPage() {
  const setAuth = useAuth((s) => s.setAuth);
  const nav = useNavigate();
  const [form, setForm] = useState({ firstName: "", lastName: "", email: "", phone: "", address: "", password: "" });
  const [loading, setLoading] = useState(false);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await api.post<AuthResponse>("/auth/register", form);
      setAuth(res.data.id, res.data.token, res.data.email, res.data.roles ?? []);
      toast.success("Welcome to PharmaOrder! +150 health points 🎉");
      nav({ to: "/" });
    } catch (err) {
      toast.error((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
    setForm((f) => ({ ...f, [k]: e.target.value }));

  return (
    <div className="mx-auto grid min-h-screen max-w-xl place-items-center px-4 py-12">
      <div className="w-full">
        <div className="mb-6 flex flex-col items-center text-center">
          <span className="flex h-12 w-12 items-center justify-center rounded-2xl bg-[image:var(--gradient-primary)] text-primary-foreground shadow-[var(--shadow-glow)]">
            <Pill className="h-6 w-6" />
          </span>
          <h1 className="mt-4 text-2xl font-bold">Create your account</h1>
          <p className="mt-1 text-sm text-muted-foreground">Get 150 health points on signup.</p>
        </div>

        <form onSubmit={submit} className="space-y-4 rounded-3xl border border-border/60 bg-card p-8 shadow-[var(--shadow-card)]">
          <div className="grid gap-4 sm:grid-cols-2">
            <div>
              <Label htmlFor="firstName">First name</Label>
              <Input id="firstName" required value={form.firstName} onChange={set("firstName")} className="mt-1" />
            </div>
            <div>
              <Label htmlFor="lastName">Last name</Label>
              <Input id="lastName" required value={form.lastName} onChange={set("lastName")} className="mt-1" />
            </div>
          </div>

          {[
            { id: "email", label: "Email", icon: Mail, type: "email" },
            { id: "phone", label: "Phone", icon: Phone, type: "tel" },
            { id: "password", label: "Password", icon: Lock, type: "password" },
          ].map(({ id, label, icon: Icon, type }) => (
            <div key={id}>
              <Label htmlFor={id}>{label}</Label>
              <div className="relative mt-1">
                <Icon className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input id={id} type={type} required value={form[id as keyof typeof form]} onChange={set(id as keyof typeof form)} className="pl-9" />
              </div>
            </div>
          ))}

          <div>
            <Label htmlFor="address">Delivery Address</Label>
            <div className="relative mt-1">
               <MapPin className="absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
               <Textarea id="address" required value={form.address} onChange={set("address")} placeholder="Enter your full address" className="min-h-[80px] pl-9" />
            </div>
          </div>

          <Button type="submit" disabled={loading} className="w-full rounded-full h-12 text-base shadow-[var(--shadow-glow)]">
            {loading ? "Creating account…" : "Create account"}
          </Button>
          <p className="text-center text-sm text-muted-foreground">
            Already have one?{" "}
            <Link to="/login" className="font-medium text-primary hover:underline">Sign in</Link>
          </p>
        </form>
      </div>
    </div>
  );
}
