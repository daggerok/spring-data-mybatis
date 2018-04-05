drop table if exists messages
;

create table messages (
  id serial not null,
  body nvarchar2 null
)
;

drop table if exists users
;

create table users (
  id serial not null,
  name nvarchar2 null
)
;
