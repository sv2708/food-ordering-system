DROP SCHEMA IF EXISTS CUSTOMER CASCADE;

CREATE SCHEMA CUSTOMER;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS "customer".customers CASCADE;


CREATE TABLE "customer".customers
(
    id         uuid NOT NULL,
    username   character varying COLLATE pg_catalog."default",
    first_name character varying COLLATE pg_catalog."default",
    last_name  character varying COLLATE pg_catalog."default",
    CONSTRAINT customer_pkey PRIMARY KEY (id)
);


