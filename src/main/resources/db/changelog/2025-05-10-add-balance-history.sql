--liquibase formatted sql

--changeset thuggeelya:1 splitStatements:false

CREATE TABLE IF NOT EXISTS user_balance_history
(
    user_id         bigint    not null unique,
    initial_balance decimal   not null default 0 check ( initial_balance >= 0 ),
    current_balance decimal,
    max_balance     decimal,
    created         timestamp not null default NOW(),
    last_updated    timestamp,
    lock            boolean   not null default false,
    increment       boolean   not null default true,
    primary key (user_id),
    foreign key (user_id) references "user" (id) on delete cascade
);

DROP INDEX IF EXISTS idx_user_balance_history_last_updated;

CREATE INDEX idx_user_balance_history_last_updated
    ON user_balance_history (last_updated);

CREATE OR REPLACE FUNCTION create_user_balance_history() RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO user_balance_history (user_id, initial_balance, current_balance, max_balance)
    VALUES (NEW.user_id, NEW.balance, NEW.balance, NEW.balance * 2.07);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER after_account_insert
    AFTER INSERT
    ON account
    FOR EACH ROW
EXECUTE FUNCTION create_user_balance_history();

CREATE OR REPLACE FUNCTION delete_user_balance_history()
    RETURNS TRIGGER AS
$$
BEGIN
    DELETE FROM user_balance_history WHERE user_id = OLD.user_id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER account_delete_trigger
    AFTER DELETE
    ON account
    FOR EACH ROW
EXECUTE FUNCTION delete_user_balance_history();
