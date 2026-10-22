import { MessageList } from "./MessageList.tsx";
import { MessageInput } from "./MessageInput.tsx";
import { useState } from "react";
import { sendMessage, type ChatMessage } from "../api";

type ChatPanelProps = {
    onError: (message: string | null) => void;
}

export function ChatPanel({ onError }: ChatPanelProps) {
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [sending, setSending] = useState<boolean>(false);

    async function handleSend(text: string) { 
        // 1. create and send user message, and set sending status as true
        const userMsg: ChatMessage = {
            id: crypto.randomUUID(),
            role: "user",
            content: text
        };
        setMessages((prev) => [...prev, userMsg]);
        setSending(true);

        // 2. return assistant replies
        try {
            const reply = await sendMessage("default", text);
            const assistantMsg: ChatMessage = {
                id: crypto.randomUUID(),
                role: "assistant",
                content: reply.reply,
                citations: reply.citations,
            };
            setMessages((prev) => [...prev, assistantMsg]); 
            
        } catch (err) {
            onError(err instanceof Error ? err.message : String(err));
        } finally { 
            setSending(false);
        }
    }

    return (
        <main className="chat-panel">
            <MessageList messages={messages} sending={ sending}></MessageList>
            <MessageInput onSend={handleSend} disabled={sending}></MessageInput>
        </main>
    );
}