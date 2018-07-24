CREATE TABLE public.reminders
(
    reminder_id integer NOT NULL DEFAULT nextval('reminders_reminder_id_seq'::regclass),
    target longtext COLLATE pg_catalog."default" NOT NULL,
    user_name longtext COLLATE pg_catalog."default" NOT NULL,
    message longtext COLLATE pg_catalog."default" NOT NULL,
    completed datetime(4),
    CONSTRAINT reminders_pkey PRIMARY KEY (reminder_id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.reminders
    OWNER to postgres;

CALL CreateSequence('public.reminders_reminder_id_seq', 1, 1);

ALTER SEQUENCE public.reminders_reminder_id_seq
    OWNER TO postgres;

CREATE TABLE public.occurrences
(
    occurrence_id integer NOT NULL DEFAULT nextval('occurrences_occurrence_id_seq'::regclass),
    reminder_id integer NOT NULL,
    occurrence datetime(4) NOT NULL,
    snoozed datetime(4),
    repeat longtext COLLATE pg_catalog."default",
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

CREATE INDEX fki_reminder_id
    ON public.occurrences USING btree
    (reminder_id)
    TABLESPACE pg_default;


CALL CreateSequence('public.occurrences_occurrence_id_seq', 1, 1);

ALTER SEQUENCE public.occurrences_occurrence_id_seq
    OWNER TO postgres;

