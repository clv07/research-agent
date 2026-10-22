import { useState } from "react";

type MessageInputProps = {
    onSend: (text: string) => void;
    disabled: boolean;
}

export function MessageInput({ onSend, disabled }: MessageInputProps) { 
    const [draft, setDraft] = useState<string>("");

    function handleClick() { 
        onSend(draft.trim());
        setDraft("");
    }
    
    return (
        <div className="message-input">
            <textarea placeholder="Type a message..." value={draft} onChange={(e) => setDraft(e.target.value)}/>
            <button type="button" onClick={handleClick} disabled={ disabled || !draft.trim()}>Send</button>
        </div>
       
    );
}