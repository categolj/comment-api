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

CREATE INDEX idx_comments_entry_id ON comment (entry_id);
CREATE INDEX idx_comments_user_id ON comment (commenter_id);
CREATE INDEX idx_comments_status ON comment (status);
