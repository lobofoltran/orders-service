ALTER TABLE outbox_events
    ADD COLUMN next_retry_at TIMESTAMP WITH TIME ZONE NULL;

DROP INDEX IF EXISTS idx_outbox_unpublished;

CREATE INDEX idx_outbox_pending
    ON outbox_events (published_at, next_retry_at, created_at)
    WHERE published_at IS NULL;

