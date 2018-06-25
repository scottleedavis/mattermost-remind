create table reminders
(
  id int identity
    primary key,
  target       nvarchar(100)  not null,
  userName     nvarchar(100)  not null,
  occurrence   date           not null,
  message .    nvarchar(1000) not null
)