create table todo (
    id serial,
    title text not null,
    completed boolean not null default false,
    "order" int not null
)