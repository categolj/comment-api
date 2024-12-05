CREATE TABLE IF NOT EXISTS commenter
(
    id      VARCHAR PRIMARY KEY,
    name    VARCHAR NOT NULL,
    email   VARCHAR NOT NULL,
    picture VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS comment
(
    comment_id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entry_id     BIGINT      NOT NULL,
    body         TEXT        NOT NULL,
    commenter_id VARCHAR     NOT NULL,
    status       VARCHAR(10) NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT status_check CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    FOREIGN KEY (commenter_id) REFERENCES commenter (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_comments_entry_id ON comment (entry_id);
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comment (commenter_id);
CREATE INDEX IF NOT EXISTS idx_comments_status ON comment (status);

-- spring modulith

CREATE TABLE IF NOT EXISTS event_publication
(
    id               UUID                     NOT NULL,
    listener_id      TEXT                     NOT NULL,
    event_type       TEXT                     NOT NULL,
    serialized_event TEXT                     NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date  TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS event_publication_serialized_event_hash_idx ON event_publication USING hash (serialized_event);
CREATE INDEX IF NOT EXISTS event_publication_by_completion_date_idx ON event_publication (completion_date);

CREATE TABLE IF NOT EXISTS event_publication_archive
(
    id               UUID                     NOT NULL,
    listener_id      TEXT                     NOT NULL,
    event_type       TEXT                     NOT NULL,
    serialized_event TEXT                     NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date  TIMESTAMP WITH TIME ZONE,
    PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS event_publication_archive_serialized_event_hash_idx ON event_publication_archive USING hash (serialized_event);
CREATE INDEX IF NOT EXISTS event_publication_archive_by_completion_date_idx ON event_publication_archive (completion_date);
