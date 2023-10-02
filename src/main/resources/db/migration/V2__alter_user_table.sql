alter table users
    add column first_name varchar(64),
    add column last_name  varchar(64);

update users set first_name = 'defaultFirstName' where users.first_name is null;
update users set last_name = 'defaultLastName' where users.last_name is null;

alter table users
    alter column first_name set NOT NULL,
    alter column last_name set NOT NULL;
