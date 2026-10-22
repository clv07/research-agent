import { useEffect, useRef, useState } from "react";
import { uploadPapers, type Paper } from "../api.ts";

type SidebarProps = {
    papers: Paper[]; // data going down
    onUploaded: (paper: Paper) => void; // function called by child
    onError: (message: string | null) => void;
};

export function Sidebar({ papers, onUploaded, onError}: SidebarProps) {
    const [selected, setSelected] = useState<File | null>(null); // to remember chosen file
    const buttonRef = useRef<HTMLButtonElement>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);

    async function handleUpload() {
        if (!selected) return; // do nothing if no file is selected
        try {
            const created = await uploadPapers(selected); // local variable when selected file is being created
            onUploaded(created);
            setSelected(null); // disable upload button after uploading file
            if (fileInputRef.current) fileInputRef.current.value = ""; // clear file input's displayed filename
            onError(null); // an upload success so an old error message disappears
        } catch (err) {
            // store error message in state and render it
            onError(err instanceof Error ? err.message : String(err));
        }
    }

    useEffect(() => { 
        if (selected) buttonRef.current?.focus(); // runs after render when the button is enabled
    }, [selected]);

    return (
        <aside className="sidebar">
            {/* file input */}
            <input
                type="file"
                accept=".pdf,.txt"
                onChange={(event) => setSelected(event.target.files?.[0] ?? null)}
                ref={ fileInputRef}
            />
            {/* upload button */}
            <button
                className="upload-button"
                type="button"
                onClick={handleUpload}
                disabled={!selected}
                ref={ buttonRef }
            >
                Upload files
            </button>

            {/* paper list */}
            <ul>
                {papers.map((p) => (
                    <li key={p.id}>
                        {p.filename} : {p.status}
                    </li>
                ))}
            </ul>
        </aside>
    );
}
