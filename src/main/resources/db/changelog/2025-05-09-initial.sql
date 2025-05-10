--liquibase formatted sql

--changeset thuggeelya:1

CREATE TABLE IF NOT EXISTS "user"
(
    id            bigserial primary key,
    date_of_birth date                   NOT NULL,
    name          character varying(500) NOT NULL,
    password      character varying(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS account
(
    id      bigserial primary key,
    balance decimal NOT NULL default 0 check ( balance >= 0 ),
    user_id bigint  NOT NULL UNIQUE,
    foreign key (user_id) references "user" (id) on delete cascade
);

CREATE TABLE IF NOT EXISTS email_data
(
    id      uuid primary key,
    email   character varying(200) NOT NULL UNIQUE,
    user_id bigint                 NOT NULL,
    foreign key (user_id) references "user" (id) on delete cascade
);

CREATE TABLE IF NOT EXISTS phone_data
(
    id      uuid primary key,
    phone   character varying(13) NOT NULL UNIQUE,
    user_id bigint                NOT NULL,
    foreign key (user_id) references "user" (id) on delete cascade
);
