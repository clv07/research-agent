
// TS type that mirrors PaperResponse record
export type Paper = {
    id: string;
    title: string | null;
    filename: string;
    uploadedAt: string;
    status: 'PENDING' | 'EMBEDDED' | 'FAILED';
}

export type ChatReply = {
    reply: string;
    citations: {
        paperId: string,
        chunkId: string
    }[];
}
export type ChatMessage = {
    id: string, 
    role: "user" | "assistant",
    content: string,
    citations?: {
        paperId: string,
        chunkId: string
    }[]; 
}

export async function uploadPapers(file: File): Promise<Paper> { 
    const body = new FormData();
    body.append('file', file);
    const res = await fetch('/api/papers', { method: 'POST', body });
    if (!res.ok)
        throw new Error(`Upload failed: ${res.status}`);
    return res.json();
}

export async function listPapers(): Promise<Paper[]> {
    const res = await fetch('/api/papers');
    if (!res.ok)
        throw new Error(`Get list of papers failed: ${res.status}`);
    return res.json();
}

// mock for now
export async function sendMessage(_conversationId: string, message: string): Promise<ChatReply>{ 
    await new Promise((r) => setTimeout(r, 800)); // pretend to think
    return {
        reply: `(mock) You said: ${message}`,
         citations: []
    };
}

