DROP SCHEMA IF EXISTS "order" CASCADE;

CREATE SCHEMA "order";

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS "order_status";

CREATE TYPE order_status as Enum ('PENDING', 'SHIPPED', 'CANCELLED',
    'CANCELLING', 'PAID', 'APPROVED');

DROP TABLE IF EXISTS "order".orders CASCADE;

CREATE TABLE "order".orders
(
    id               uuid           NOT NULL,
    customer_id      uuid           NOT NULL,
    restaurant_id    uuid           NOT NULL,
    tracking_id      uuid           NOT NULL,
    price            NUMERIC(10, 2) NOT NULL,
    order_status     order_status   NOT NULL,
    failure_messages character varying COLLATE pg_catalog."default",
    CONSTRAINT order_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "order".order_items CASCADE;

CREATE TABLE "order".order_items
(
    id         bigint         NOT NULL,
    order_id   uuid           NOT NULL,
    product_id uuid           NOT NULL,
    price      NUMERIC(10, 2) NOT NULL,
    quantity   INTEGER        NOT NULL,
    sub_total  NUMERIC(10, 2) NOT NULL,
    CONSTRAINT order_items_pkey PRIMARY KEY (id, order_id)
);

ALTER TABLE "order".order_items
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id)
        REFERENCES "order".orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;

DROP TABLE IF EXISTS "order".order_address CASCADE;

CREATE TABLE "order".order_address
(
    id            uuid                                           NOT NULL,
    order_id      uuid UNIQUE                                    NOT NULL,
    address_line1 character varying COLLATE pg_catalog."default" NOT NULL,
    address_line2 character varying COLLATE pg_catalog."default",
    city          character varying COLLATE pg_catalog."default" NOT NULL,
    zipcode       character varying COLLATE pg_catalog."default" NOT NULL
);

ALTER TABLE "order".order_address
    ADD CONSTRAINT "FK_ORDER_ID" FOREIGN KEY (order_id)
        REFERENCES "order".orders (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID;


drop type if exists SAGA_STATUS;
create type SAGA_STATUS as enum ('STARTED', 'FAILED', 'SUCCEEDED', 'PROCESSING', 'COMPENSATING', 'COMPENSATED');

drop type if exists OUTBOX_STATUS;
create type OUTBOX_STATUS as enum ('STARTED','PROCESSING', 'COMPLETED');


drop table if exists "order".payment_outbox cascade;

create table "order".payment_outbox
(
    id            uuid                                           not null,
    saga_id       uuid                                           not null,
    created_at    TIMESTAMP with TIME ZONE                       not null,
    processed_at  TIMESTAMP with TIME ZONE                       not null,
    type          character varying collate "pg_catalog".default not null,
    payload       jsonb                                          not null,
    outbox_status outbox_status                                  not null,
    saga_status   saga_status                                    not null,
    order_status  order_status                                   not null,
    version       integer                                        not null,
    constraint payment_outbox_pkey primary key (id)
);


create index "payment_outbox_order_saga_status"
    on "order".payment_outbox
        (type, outbox_status, saga_status);

create unique index "payment_outbox_saga_id"
    on "order".payment_outbox
        (type, saga_id, saga_status);

drop table if exists "order".restaurant_approval_outbox cascade;

create table "order".restaurant_approval_outbox
(
    id            uuid                                           not null,
    saga_id       uuid                                           not null,
    created_at    TIMESTAMP with TIME ZONE                       not null,
    processed_at  TIMESTAMP with TIME ZONE                       not null,
    type          character varying collate "pg_catalog".default not null,
    payload       jsonb                                          not null,
    outbox_status outbox_status                                  not null,
    saga_status   saga_status                                    not null,
    order_status  order_status                                   not null,
    version       integer                                        not null,
    constraint restaurant_outbox_pkey primary key (id)
);


create index "restaurant_approval_outbox_order_saga_status"
    on "order".payment_outbox
        (type, outbox_status, saga_status);

create unique index "restaurant_approval_outbox_saga_id"
    on "order".payment_outbox
        (type, saga_id, saga_status);

