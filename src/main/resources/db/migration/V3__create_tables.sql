-- Tables table
CREATE TABLE tables (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    floor_id        UUID NOT NULL REFERENCES floors(floor_id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    min_bet         NUMERIC(19, 2) NOT NULL,
    state           VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    player_count    INTEGER NOT NULL DEFAULT 0,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index for faster queries
CREATE INDEX idx_tables_floor_id_state ON tables(floor_id, state);
