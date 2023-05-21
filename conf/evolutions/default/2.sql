
-- !Ups

create table ecommerce.users
(
    id             serial       not null constraint users_pk primary key,
    first_name     varchar(64)  not null,
    last_name      varchar(64)  not null,
    password       varchar(128) not null,
    email          varchar(100) not null,
    role           varchar(16)  not null,
    birth_date     date,
    address        varchar(255),
    phone_number   varchar(20)
);

create unique index users_id_uindex
    on ecommerce.users (id);

create unique index users_email_uindex
    on ecommerce.users (email);

create table ecommerce.posts
(
	id              serial          not null constraint posts_pk primary key,
	author          serial          not null,
	title           varchar(128)    not null,
	description     varchar(500),
	content         text            not null,
	date            timestamp       not null
);

create unique index posts_id_uindex
    on ecommerce.posts (id);


create table ecommerce.products
(
    id              serial       not null constraint products_pk primary key,
    product_name    varchar(64)  not null,
    price           float(53),
    exp_date        date
);

create unique index products_id_uindex
    on ecommerce.products (id);


create table ecommerce.orders
(
    id              serial       not null constraint orders_pk primary key,
    user_id         bigint       not null,
    order_date      timestamp    not null,
    total_price     float(53)    not null
);

create unique index orders_id_uindex
    on ecommerce.orders (id);

create table ecommerce.order_details
(
    id              serial       not null constraint order_details_pk primary key,
    order_id        bigint       not null,
    product_id      bigint       not null,
    quantity        bigint       not null,
    price           float(53)    not null
);

create unique index order_details_id_uindex
    on ecommerce.order_details (id);


alter table if exists orders add constraint FKkud35ls1d40wpjb5htpp14q4e foreign key (user_id) references users;
alter table if exists order_details add constraint FK2k3smhbruedlcrvu6clued06x foreign key (order_id) references orders;
alter table if exists order_details add constraint FK2k3smhbruedlcrvu6clued06i foreign key (product_id) references products;


-- !Downs

DROP TABLE ecommerce.users;
DROP TABLE ecommerce.posts;
DROP TABLE ecommerce.orders;
DROP TABLE ecommerce.order_details;
DROP TABLE ecommerce.products;
