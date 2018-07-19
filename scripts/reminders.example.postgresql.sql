-- Table: public.reminders

-- DROP TABLE public.reminders;

CREATE TABLE public.reminders
(
    reminder_id integer NOT NULL DEFAULT nextval('reminders_reminder_id_seq'::regclass),
    target text COLLATE pg_catalog."default" NOT NULL,
    user_name text COLLATE pg_catalog."default" NOT NULL,
    message text COLLATE pg_catalog."default" NOT NULL,
    completed timestamp(4) with time zone,
    CONSTRAINT reminders_pkey PRIMARY KEY (reminder_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.reminders
    OWNER to postgres;

-- SEQUENCE: public.reminders_reminder_id_seq

-- DROP SEQUENCE public.reminders_reminder_id_seq;

CREATE SEQUENCE public.reminders_reminder_id_seq;

ALTER SEQUENCE public.reminders_reminder_id_seq
    OWNER TO postgres;

    

-- Table: public.occurrences

-- DROP TABLE public.occurrences;

CREATE TABLE public.occurrences
(
    occurrence_id integer NOT NULL DEFAULT nextval('occurrences_occurrence_id_seq'::regclass),
    reminder_id integer NOT NULL,
    occurrence timestamp(4) with time zone NOT NULL,
    snoozed timestamp(4) with time zone,
    repeat text COLLATE pg_catalog."default",
    CONSTRAINT occurrences_pkey PRIMARY KEY (occurrence_id),
    CONSTRAINT reminder_id FOREIGN KEY (reminder_id)
        REFERENCES public.reminders (reminder_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.occurrences
    OWNER to postgres;

-- Index: fki_reminder_id

-- DROP INDEX public.fki_reminder_id;

CREATE INDEX fki_reminder_id
    ON public.occurrences USING btree
    (reminder_id)
    TABLESPACE pg_default;


-- SEQUENCE: public.occurrences_occurrence_id_seq

-- DROP SEQUENCE public.occurrences_occurrence_id_seq;

CREATE SEQUENCE public.occurrences_occurrence_id_seq;

ALTER SEQUENCE public.occurrences_occurrence_id_seq
    OWNER TO postgres;

