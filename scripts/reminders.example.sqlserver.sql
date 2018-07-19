create table reminders
(
  id int identity primary key,
  target       nvarchar(100)  not null,
  user_name    nvarchar(100)  not null,
  message     nvarchar(1000) not null,
  completed    date
)

create table occurrences
(
  id int identity primary key,
  reminder_id  int not null,
  occurrence    date,
  snoozed date,
  repeat nvarchar(100)
)
