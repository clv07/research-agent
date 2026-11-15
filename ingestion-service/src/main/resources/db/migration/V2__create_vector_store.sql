CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE vector_store (
    id uuid PRIMARY KEY,
    content text,
    metadata json,
    embedding vector(384)  -- all-MiniLM-L6-v2 outputs 384 numbers
);


CREATE INDEX spring_ai_vector_index
    ON vector_store USING hnsw (embedding vector_cosine_ops);
