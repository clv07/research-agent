import { useState, type KeyboardEvent } from "react";

type MessageInputProps = {
    onSend: (text: string) => void;
    disabled: boolean;
}

export function MessageInput({ onSend, disabled }: MessageInputProps) {
    const [draft, setDraft] = useState<string>("");
    const canSend = !disabled && draft.trim() !== "";

    function handleClick() {
        onSend(draft.trim());
        setDraft("");
    }

    function handleKeyDown(e: KeyboardEvent<HTMLTextAreaElement>) {
        if (e.key !== "Enter" || e.shiftKey) return; // let browser handle for other keys 
        if (e.nativeEvent.isComposing || e.keyCode === 229) return; // Enter that confirms Chinese input (IME) characters
        e.preventDefault(); // default pressing "Enter" will insert a new line
        if (canSend) handleClick();
    }

    return (
        <div className="message-input">
            <textarea
                placeholder="Type a message..."
                value={draft}
                onChange={(e) => setDraft(e.target.value)}
                onKeyDown={handleKeyDown}
            />
            <button
                type="button"
                onClick={handleClick}
                disabled={!canSend}>
                Send
            </button>
        </div>

    );
}