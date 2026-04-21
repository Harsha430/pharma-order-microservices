import { useState } from "react";
import { FileText, Upload, CheckCircle2, Loader2, X } from "lucide-react";
import { api } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { useAuth } from "@/stores/auth";

interface Props {
  onSuccess: (prescriptionId: string) => void;
}

export function PrescriptionUpload({ onSuccess }: Props) {
  const { user } = useAuth();
  const [file, setFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const [uploadedId, setUploadedId] = useState<string | null>(null);

  const handleUpload = async () => {
    if (!user) {
      toast.error("Please sign in to upload prescriptions");
      return;
    }

    setUploading(true);
    try {
      // Step 1: Upload actual file to file-service
      const formData = new FormData();
      formData.append("file", file!);

      const fileRes = await api.post("/files/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      const { fileKey } = fileRes.data;

      // Step 2: Create prescription record metadata
      const payload = {
        userId: user.id,
        fileKey: fileKey,
        originalFilename: file!.name,
      };

      const res = await api.post("/prescriptions/upload", payload);
      const prescId = res.data.id;
      
      setUploadedId(prescId);
      onSuccess(prescId);
      toast.success("Prescription uploaded and verified!");
    } catch (e) {
      toast.error("Failed to process prescription. Please try again.");
      console.error(e);
    } finally {
      setUploading(false);
    }
  };

  if (uploadedId) {
    return (
      <div className="flex items-center gap-3 rounded-2xl border border-primary/20 bg-primary/5 p-4 animate-in fade-in slide-in-from-bottom-2">
        <CheckCircle2 className="h-5 w-5 text-primary" />
        <div className="flex-1">
          <p className="text-sm font-semibold text-primary">Prescription Validated</p>
          <p className="text-xs text-primary/70 italic line-clamp-1">{file?.name || "Uploaded document"}</p>
        </div>
        <Button variant="ghost" size="icon" className="h-8 w-8 text-primary/60" onClick={() => setUploadedId(null)}>
          <X className="h-4 w-4" />
        </Button>
      </div>
    );
  }

  return (
    <div className="rounded-2xl border border-dashed border-border/60 bg-card p-5">
      <div className="flex items-center gap-4">
        <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-secondary text-primary shrink-0">
          <FileText className="h-6 w-6" />
        </div>
        <div className="flex-1">
          <p className="text-sm font-semibold">Upload Prescription</p>
          <p className="text-xs text-muted-foreground">Required for this medication (PDF, JPG)</p>
        </div>
      </div>

      <div className="mt-4 flex gap-2">
        <div className="relative flex-1">
          <input
            type="file"
            className="absolute inset-0 z-10 cursor-pointer opacity-0"
            onChange={(e) => setFile(e.target.files ? e.target.files[0] : null)}
            accept=".pdf,.jpg,.jpeg,.png"
          />
          <Button variant="outline" className="w-full justify-start rounded-xl border-border/40 font-normal">
             {file ? file.name : "Select file..."}
          </Button>
        </div>
        <Button 
          disabled={!file || uploading} 
          onClick={handleUpload}
          className="rounded-xl px-5"
        >
          {uploading ? <Loader2 className="h-4 w-4 animate-spin" /> : <Upload className="h-4 w-4" />}
        </Button>
      </div>
    </div>
  );
}
