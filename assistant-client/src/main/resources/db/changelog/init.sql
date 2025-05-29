--liquibase formatted sql

--changelog thuggeelya:1

CREATE TABLE IF NOT EXISTS chat_logs
(
    id        UUID PRIMARY KEY,
    timestamp TIMESTAMP,
    question  TEXT,
    prompt    TEXT,
    response  TEXT,
    source    VARCHAR(50)
);
