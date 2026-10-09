-- Declare table and define columns
CREATE TABLE papers (
    id uuid PRIMARY KEY,
    title TEXT,
    filename TEXT NOT NULL,
    uploaded_at TIMESTAMPTZ NOT NULL,
    status varchar NOT NULL
);

