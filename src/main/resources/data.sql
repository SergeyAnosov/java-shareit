delete from users;
delete from items;
delete from bookings;
delete from comments;
--delete from requests;


ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE items ALTER COLUMN id RESTART WITH 1;
ALTER TABLE bookings ALTER COLUMN id RESTART WITH 1;
ALTER TABLE comments ALTER COLUMN id RESTART WITH 1;
--ALTER TABLE requests ALTER COLUMN id RESTART WITH 1;

