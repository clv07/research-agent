import { MessageList } from "./MessageList.tsx";
import { MessageInput } from "./MessageInput.tsx";

export function ChatPanel() {
    return (
        <main className="chat-panel">
            <div>
                <MessageList></MessageList>
                <MessageInput></MessageInput>
            </div>
        </main>
    );
}