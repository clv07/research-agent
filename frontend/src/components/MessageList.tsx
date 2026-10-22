import { type ChatMessage } from "../api.ts";

const sampleMessage: ChatMessage[] = [
    { id: "1", role: "user", content: "Write me code for this project." },
    { id: "2", role: "assistant", content: "Sure! Gimme a secs...", citations: [{ paperId: "1", chunkId: "1" }] }
];

export function MessageList() {

    return (
        <div className="message-list">
                {sampleMessage.map((m) => (
                    <div key={m.id} className={`message message-${m.role}`}>{m.content}</div>
                ))}
        </div>
    );
}