import { useState, useEffect } from "react";
import "./App.css";
import { uploadPapers, listPapers, type Paper } from "./api.ts";

function App() {
  // 1. states
  const [selected, setSelected] = useState<File | null>(null); // to remember chosen file
  const [papers, setPapers] = useState<Paper[]>([]); // to track list of papers
  const [error, setError] = useState<string | null>(null); // to display error

  // 2. effects
  useEffect(() => {
    listPapers()
      .then(setPapers)
      .catch((e) => setError(e.message));
  }, []);

  // 3. handlers for file upload action
  async function handleUpload() {
    if (!selected) return; // do nothing if no file is selected
    try {
      const created = await uploadPapers(selected); // local variable when selected file is being created
      setError(null); // an upload success so an old error message disappears
      setPapers((prev) => [...prev, created]); // create new array by adding created to existing list
      // for React to notice a change and render
    } catch (err) {
      // store error message in state and render it
      setError(err instanceof Error ? err.message : String(err));
    }
  }

  // 4. render the UI
  return (
    <>
      {/* file input */}
      <input
        type="file"
        accept=".pdf,.txt"
        onChange={(event) => setSelected(event.target.files?.[0] ?? null)}
      />
      {/* upload button */}
      <button type="button" onClick={handleUpload} disabled={!selected}>
        Upload files
      </button>
      {/* error message (render only when an error is set) */}
      {error && <p>{error}</p>}

      {/* paper list */}
      <ul>
        {papers.map((p) => (
          <li key={p.id}>
            {p.filename} : {p.status}
          </li>
        ))}
      </ul>
    </>
  );
}

export default App;
