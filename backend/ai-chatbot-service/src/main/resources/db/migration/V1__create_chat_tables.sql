CREATE TABLE chat_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    session_title VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_active TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    tokens_used INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE chat_rate_limits (
    user_id UUID PRIMARY KEY,
    message_count INT NOT NULL DEFAULT 0,
    window_start TIMESTAMP NOT NULL
);
