import { useRef, useEffect } from "react";
import { type ChatMessage } from "../api.ts";

type MessageListProps = {
    messages: ChatMessage[];
    sending: boolean;
}

export function MessageList({ messages, sending }: MessageListProps) {
    const bottomRef = useRef<HTMLDivElement>(null);

    // for auto scrolling the conversation panel to the bottom
    useEffect(() => { 
        bottomRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages, sending]);

    return (
        <div className="message-list">
                {messages.map((m) => (
                    <div key={m.id} className={`message message-${m.role}`}>{m.content}</div>
                ))}
            {sending && <div className="message message-assistant">Thinking…</div>}
            <div ref={bottomRef} /> 
        </div>
    );
}