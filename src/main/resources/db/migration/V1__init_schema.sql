-- Players table
CREATE TABLE players (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username    VARCHAR(255) NOT NULL UNIQUE,
    avatar_url  VARCHAR(512),
    balance     NUMERIC(19, 2) NOT NULL DEFAULT 500,
    status      VARCHAR(50)  NOT NULL DEFAULT 'OFFLINE'
);

-- Buildings table
CREATE TABLE buildings (
    id UUID PRIMARY KEY
);

-- Floors table
CREATE TABLE floors (
    floor_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    building_id UUID NOT NULL REFERENCES buildings(id) ON DELETE CASCADE,
    name        VARCHAR(255) NOT NULL,
    icon        VARCHAR(255) NOT NULL,
    route       VARCHAR(255) NOT NULL,
    floor_order INTEGER
);
