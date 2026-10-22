import { useEffect, useState } from "react";
import "./App.css";
import { listPapers, type Paper } from "./api.ts";
import { Sidebar } from "./components/Sidebar.tsx";
import { ChatPanel } from "./components/ChatPanel.tsx";
import { Toast } from "./components/Toast.tsx";

function App() {
  // 1. states
  const [papers, setPapers] = useState<Paper[]>([]); // to track list of papers
  const [error, setError] = useState<string | null>(null); // to display error

  // 2. effects
  useEffect(() => {
    listPapers()
      .then(setPapers)
      .catch((e) => setError(e.message));
  }, []);

  // render the UI
  return (
    <div className="app">
      <Sidebar papers={papers} onUploaded={(paper) => setPapers((prev) => [...prev, paper])} onError={setError}></Sidebar>
      <ChatPanel />
      <Toast message={ error } onClose={setError} />
    </div>

  );
}

export default App;
