create table users
(
    id         serial primary key,
    email      varchar(64) not null unique,
    username   varchar(64) not null unique,
    password   varchar(64) not null,
    role       varchar(32) not null,
    enabled    boolean     not null default true,
    created_at timestamp   not null default now(),
    updated_at timestamp   not null default now()
);

create table tokens
(
    id         serial primary key,
    token      varchar(64) not null unique,
    type       varchar(32) not null,
    user_id    bigint      not null,
    created_at timestamp   not null default now(),
    expire_at  timestamp   not null,
    foreign key (user_id) references users (id) on delete cascade
);
