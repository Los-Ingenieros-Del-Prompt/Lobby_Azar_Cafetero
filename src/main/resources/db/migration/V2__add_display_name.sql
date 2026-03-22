-- Add display_name column to store the player's real name from Google
ALTER TABLE players ADD COLUMN display_name VARCHAR(255);

-- Backfill existing rows with the username value as fallback
UPDATE players SET display_name = username WHERE display_name IS NULL;
