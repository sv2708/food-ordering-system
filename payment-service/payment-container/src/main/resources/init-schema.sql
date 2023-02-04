DROP SCHEMA IF EXISTS payment CASCADE;

CREATE SCHEMA payment;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TYPE IF EXISTS payment_status;

CREATE TYPE payment_status AS ENUM ('COMPLETED', 'CANCELLED', 'FAILED', 'PENDING', 'ORDER_CANCELLED');

DROP TABLE IF EXISTS "payment".payments CASCADE;

CREATE TABLE "payment".payments
(
    id          uuid                     NOT NULL,
    customer_id uuid                     NOT NULL,
    order_id    uuid                     NOT NULL,
    amount      numeric(10, 2)           NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    status      payment_status           NOT NULL,
    CONSTRAINT payments_pkey PRIMARY KEY (id)
);

DROP TABLE IF EXISTS "payment".credit_entry CASCADE;

CREATE TABLE "payment".credit_entry
(
    id                  uuid           NOT NULL,
    customer_id         uuid           NOT NULL,
    total_credit_amount numeric(10, 2) NOT NULL,
    CONSTRAINT credit_entry_pkey PRIMARY KEY (id)
);

DROP TYPE IF EXISTS transaction_type;

CREATE TYPE transaction_type AS ENUM ('DEBIT', 'CREDIT');

DROP TABLE IF EXISTS "payment".credit_history CASCADE;

CREATE TABLE "payment".credit_history
(
    id          uuid             NOT NULL,
    customer_id uuid             NOT NULL,
    amount      numeric(10, 2)   NOT NULL,
    type        transaction_type NOT NULL,
    CONSTRAINT credit_history_pkey PRIMARY KEY (id)
);

DROP TYPE IF EXISTS outbox_status;
CREATE TYPE outbox_status as ENUM ('STARTED', 'COMPLETED', 'FAILED');


DROP TABLE IF EXISTS "payment".order_outbox CASCADE;
create table "payment".order_outbox
(
    id             uuid                                           not null,
    saga_id        uuid                                           not null,
    created_at     TIMESTAMP with TIME ZONE                       not null,
    processed_at   TIMESTAMP with TIME ZONE                       not null,
    type           character varying collate "pg_catalog".default not null,
    payload        jsonb                                          not null,
    outbox_status  outbox_status                                  not null,
    payment_status payment_status                                 not null,
    version        integer                                        not null,
    constraint order_outbox_pkey primary key (id)
);

create index "order_outbox_payment_status_index"
    on "payment".order_outbox (type, payment_status);

create unique index "payment_status_saga_id_outbox_status_index" on
    "payment".order_outbox (type, saga_id, payment_status, outbox_status);
