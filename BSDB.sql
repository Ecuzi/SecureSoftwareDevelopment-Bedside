create database BSDB;
create table people
(
	full_name varchar(256) not null,
    email_address varchar(256) not null,
    phone_number varchar(10),
    passwd varchar(256) not null,
    access_level int not null,
    primary key (email_address)
);

# levels:
# 0: guest
# 1: service
# 2: front desk

insert into people values
("admin", "bedsidespam@gmail.com", "8000400020", "password1", 2),
("service", "nolanjgrcode@gmail.com", "8000400020", "password2", 1);
create table rooms
(
	room_number int unique not null auto_increment,
    reserved boolean not null,
    primary key(room_number)
);

insert into rooms(reserved) values
(0),	# 1
(0),	# 2
(0),	# 3
(0),	# 4
(0),	# 5
(0),	# 6
(0),	# 7
(0),	# 8
(0);	# 9

create table reservations
(
	id int unique not null auto_increment,
    guest_name varchar(256) not null,
    email_address varchar(256) not null,			# email address
    phone_number varchar(256),
    room_number int not null,
    room_service boolean not null,
    cleaning_service boolean not null,
    primary key(id)
);
select * from people;