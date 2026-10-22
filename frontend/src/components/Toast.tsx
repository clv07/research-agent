import { useEffect } from "react";

type ToastProps = {
    message: string | null;
    onClose: (message: string | null) => void;
}

export function Toast({ message, onClose }: ToastProps) { 

    useEffect(() => { 
        if (!message) return;  // nothing to do
        const id = setTimeout(() => onClose(null), 3000); // start timer, remember its id
        return () => clearTimeout(id); // cleanup by cancelling that timer
    }, [message, onClose]);

    if (!message)
        return null;

    return (
        <div className="toast" role="alert">
            {message}
            <button className="close-button" onClick={() => onClose(null)}>x</button>
        </div>
    );
}