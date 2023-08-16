drop table if exists comments, compilations_events, compilations, event_requests, events, categories , users;

create table if not exists categories (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(50) NOT NULL,
    constraint pk_categories primary key (id)
);
create unique index if not exists CATEGORIES_ID_UINDEX on categories (id);
create unique index if not exists CATEGORIES_NAME_UINDEX on categories (name);

create table if not exists users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name varchar(250) NOT NULL,
    email varchar(254) NOT NULL,
    constraint pk_users primary key (id)
);
create unique index if not exists USERS_ID_UINDEX on users(id);
create unique index if not exists USERS_NAME_UINDEX on users(name);
create unique index if not exists USERS_MAIL_UINDEX on users(email);

create table if not exists events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation varchar NOT NULL CHECK (LENGTH(annotation) >= 20 AND LENGTH(annotation) <= 2000 ),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    created_on timestamp WITHOUT TIME ZONE NOT NULL,
    description VARCHAR NOT NULL CHECK (LENGTH(description) >= 20 AND LENGTH(description) <= 7000 ) ,
    event_date timestamp WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL REFERENCES users(id),
    location BYTEA NOT NULL,
    paid boolean NOT NULL DEFAULT false,
    participant_limit int DEFAULT 0,
    published_on timestamp WITHOUT TIME ZONE ,
    request_moderation boolean DEFAULT true,
    state varchar NOT NULL,
    title varchar NOT NULL  CHECK (LENGTH(title) >= 3 AND LENGTH(title) <= 120 ) ,
    constraint pk_events primary key (id)
);

create table if not exists event_requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created timestamp WITHOUT TIME ZONE NOT NULL,
    event_id BIGINT NOT NULL REFERENCES events(id),
    requester_id BIGINT NOT NULL REFERENCES users(id),
    status varchar NOT NULL,
    constraint pk_event_requests primary key (id)
);

create table if not exists compilations (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    pinned boolean NOT NULL,
    title varchar(50) Not null,
    constraint pk_compilations primary key (id)
);

create table if not exists compilations_events (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL REFERENCES events(id),
    compilation_id BIGINT NOT NULL REFERENCES compilations(id),
    constraint pk_compilations_events primary key (id)
);

create table if not exists comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT NOT NULL REFERENCES events(id),
    commentator_id BIGINT NOT NULL REFERENCES users(id),
    comment_text varchar NOT NULL CHECK (LENGTH(comment_text) >= 1 AND LENGTH(comment_text) <= 5000 ) ,
    created_date timestamp WITHOUT TIME ZONE Not Null,
    constraint pk_comments primary key (id)
);