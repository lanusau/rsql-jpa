CREATE TABLE manufacturer (
    id int not null,
    name varchar(100) not null
);

CREATE TABLE model (
    id int not null,
    manufacturer_id int not null,
    year int not null,
    name varchar(100) not null
);

CREATE TABLE car (
    id int not null,
    model_id int not null,
    color varchar(20) not null,
    manufacture_date timestamp not null,
    electric smallint,
    date_sold timestamp
);
