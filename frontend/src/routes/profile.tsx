import { createFileRoute, Link } from "@tanstack/react-router";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useState, useEffect } from "react";
import { Heart, Mail, Phone, Shield, User as UserIcon, MapPin, Edit3, Save, X } from "lucide-react";
import { api, updateProfile, type Profile } from "@/lib/api";
import { useAuth } from "@/stores/auth";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";

export const Route = createFileRoute("/profile")({
  head: () => ({ meta: [{ title: "My Profile — PharmaOrder" }] }),
  component: ProfilePage,
});

function ProfilePage() {
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({ firstName: "", lastName: "", phone: "", address: "" });

  const { data, isLoading, refetch } = useQuery({
    queryKey: ["profile"],
    queryFn: async () => (await api.get<Profile>("/users/me")).data,
    enabled: !!user,
    retry: false,
  });

  const { data: loyalty } = useQuery({
    queryKey: ["loyalty", user?.id],
    queryFn: async () => (await api.get<{totalPoints: number}>(`/loyalty/user/${user!.id}`)).data,
    enabled: !!user?.id,
  });

  useEffect(() => {
    if (data) {
      setEditForm({
        firstName: data.firstName || "",
        lastName: data.lastName || "",
        phone: data.phone || "",
        address: data.address || "",
      });
    }
  }, [data]);

  const updateMutation = useMutation({
    mutationFn: updateProfile,
    onSuccess: () => {
      toast.success("Profile updated successfully!");
      setIsEditing(false);
      queryClient.invalidateQueries({ queryKey: ["profile"] });
    },
    onError: (err) => {
      toast.error(err.message);
    },
  });

  if (!user) {
    return (
      <div className="mx-auto max-w-2xl px-4 py-20 text-center">
        <h1 className="text-2xl font-bold">Sign in to view your profile</h1>
        <Link to="/login"><Button className="mt-6 rounded-full">Sign in</Button></Link>
      </div>
    );
  }

  const fullName = data?.firstName ? `${data.firstName} ${data.lastName || ""}`.trim() : user.email.split("@")[0];
  const points = loyalty?.totalPoints ?? 0;

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    updateMutation.mutate(editForm);
  };

  return (
    <div className="mx-auto max-w-5xl px-4 py-12 md:px-8">
      <div className="grid gap-8 md:grid-cols-[1fr_320px]">
        <div className="rounded-[40px] border border-border/60 bg-card p-1 md:p-1.5 shadow-[var(--shadow-card)] overflow-hidden">
          <div className="bg-background/50 rounded-[34px] p-8 md:p-10 h-full">
            <div className="flex flex-wrap items-start justify-between gap-6">
              <div className="flex items-center gap-5">
                <span className="flex h-20 w-20 items-center justify-center rounded-3xl bg-[image:var(--gradient-primary)] text-3xl font-bold uppercase text-primary-foreground shadow-[var(--shadow-glow)]">
                  {fullName.charAt(0)}
                </span>
                <div>
                  <h1 className="text-3xl font-bold tracking-tight capitalize">{fullName}</h1>
                  <p className="text-base text-muted-foreground">{user.email}</p>
                </div>
              </div>
              
              {!isEditing && (
                <Button onClick={() => setIsEditing(true)} variant="outline" className="rounded-2xl gap-2 h-11 px-5 border-primary/20 hover:bg-primary/5 hover:text-primary transition-all">
                  <Edit3 className="h-4 w-4" /> Edit Profile
                </Button>
              )}
            </div>

            <form onSubmit={handleSave} className="mt-12">
              <div className="grid gap-6 md:grid-cols-2">
                {[
                  { icon: UserIcon, label: "First Name", key: "firstName", placeholder: "Enter first name" },
                  { icon: UserIcon, label: "Last Name", key: "lastName", placeholder: "Enter last name" },
                  { icon: Phone, label: "Phone Number", key: "phone", placeholder: "+1 (555) 000-0000" },
                  { icon: MapPin, label: "Delivery Address", key: "address", placeholder: "Your full street address", isFull: true },
                ].map((item) => (
                  <div key={item.key} className={`${item.isFull ? 'md:col-span-2' : ''} group`}>
                    <div className="flex items-center gap-2 mb-2 px-1">
                      <item.icon className="h-3.5 w-3.5 text-primary" />
                      <Label className="text-xs uppercase font-bold tracking-widest text-muted-foreground/80">{item.label}</Label>
                    </div>
                    
                    {isEditing ? (
                       item.isFull ? (
                        <Textarea 
                          value={editForm.address} 
                          onChange={e => setEditForm(prev => ({...prev, address: e.target.value}))}
                          className="rounded-2xl border-border/60 bg-secondary/20 min-h-[100px] focus:bg-background transition-colors"
                          placeholder={item.placeholder}
                        />
                       ) : (
                        <Input 
                          value={editForm[item.key as keyof typeof editForm]} 
                          onChange={e => setEditForm(prev => ({...prev, [item.key]: e.target.value}))}
                          className="rounded-2xl border-border/60 bg-secondary/20 h-14 px-5 focus:bg-background transition-colors"
                          placeholder={item.placeholder}
                        />
                       )
                    ) : (
                      <div className="rounded-2xl bg-secondary/30 p-5 min-h-[56px] flex items-center border border-transparent group-hover:border-primary/5 transition-all">
                        <span className="font-semibold text-foreground/90">
                           {isLoading ? "Loading..." : (data?.[item.key as keyof Profile] || "—")}
                        </span>
                      </div>
                    )}
                  </div>
                ))}
              </div>

              {isEditing && (
                <div className="mt-10 flex items-center gap-4">
                  <Button type="submit" disabled={updateMutation.isPending} className="flex-1 h-14 rounded-2xl gap-2 shadow-[var(--shadow-glow)]">
                    <Save className="h-4 w-4" /> {updateMutation.isPending ? "Saving..." : "Save Changes"}
                  </Button>
                  <Button type="button" variant="ghost" onClick={() => setIsEditing(false)} className="h-14 px-6 rounded-2xl gap-2 text-muted-foreground">
                    <X className="h-4 w-4" /> Cancel
                  </Button>
                </div>
              )}
            </form>

            <div className="mt-12 pt-8 border-t border-border/40">
               <div className="flex items-center gap-3 text-muted-foreground/60">
                 <Shield className="h-4 w-4" />
                 <span className="text-sm font-medium">Account Security: {data?.roles?.join(", ") || "ROLE_CUSTOMER"}</span>
               </div>
            </div>
          </div>
        </div>

        <aside className="h-fit rounded-[40px] bg-[image:var(--gradient-primary)] p-10 text-primary-foreground shadow-[var(--shadow-glow)] relative overflow-hidden group">
          <div className="absolute -right-10 -top-10 h-40 w-40 rounded-full bg-white/10 blur-3xl transition-all group-hover:scale-110" />
          
          <div className="relative z-10">
            <Heart className="h-10 w-10 mb-6" />
            <p className="text-base font-medium opacity-80 uppercase tracking-widest">Health Points</p>
            <p className="mt-2 text-7xl font-bold tracking-tighter">{points}</p>
            <p className="mt-2 text-sm font-medium opacity-80 uppercase tracking-widest">Value: ₹{(points / 10).toFixed(2)}</p>
            <p className="mt-8 text-sm leading-relaxed opacity-90 font-medium">
              You're doing great! Keep ordering from PharmaOrder to earn points and claim exclusive discounts.
            </p>
            <Link to="/catalog">
              <Button variant="secondary" className="mt-10 w-full h-14 rounded-2xl text-base font-bold shadow-soft">
                Shop & earn
              </Button>
            </Link>
          </div>
        </aside>
      </div>
    </div>
  );
}
