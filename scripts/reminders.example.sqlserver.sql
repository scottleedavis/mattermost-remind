if object_id('reminders', 'U') is not null
begin
    DROP TABLE reminders;
end
CREATE TABLE dbo.reminders
(
    reminder_id integer NOT NULL DEFAULT (next value for reminders_reminder_id_seq),
    target varchar(40) NOT NULL,           
    user_name varchar(40) NOT NULL,           
    message nvarchar(4000) NOT NULL,            
    completed datetime default getutcdate(),
    CONSTRAINT reminders_pkey PRIMARY KEY (reminder_id)
)

CREATE TABLE dbo.occurrences
(
    occurrence_id integer NOT NULL DEFAULT (next value for occurrences_occurrence_id_seq),
    reminder_id integer NOT NULL,
    occurrence datetime,--timestamp(4) with time zone NOT NULL,
    snoozed datetime,--timestamp(4) with time zone,
    repeat text,-- COLLATE pg_catalog."default",
    CONSTRAINT occurrences_pkey PRIMARY KEY (occurrence_id),
    CONSTRAINT reminder_id FOREIGN KEY (reminder_id)
        REFERENCES dbo.reminders (reminder_id) --MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

CREATE INDEX fki_reminder_id
    ON dbo.occurrences 
    (reminder_id)