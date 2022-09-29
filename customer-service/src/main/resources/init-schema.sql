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

DROP MATERIALIZED VIEW IF EXISTS "customer".order_customer_m_view;

CREATE MATERIALIZED VIEW "customer".order_customer_m_view
    TABLESPACE pg_default
AS
select id,
       username,
       first_name,
       last_name
from "customer".customers
WITH DATA;

refresh materialized view "customer".order_customer_m_view;

DROP function IF EXISTS "customer".refresh_order_customer_m_view;

CREATE OR REPLACE function "customer".refresh_order_customer_m_view()
    returns trigger
AS
'
    BEGIN
        refresh materialized view "customer".order_customer_m_view;
        return null;
    END
' LANGUAGE plpgsql;

CREATE trigger refresh_order_customer_m_view_trigger
    after INSERT OR UPDATE OR DELETE OR TRUNCATE
    on
        "customer".customers
    FOR each statement
EXECUTE PROCEDURE "customer".refresh_order_customer_m_view();

