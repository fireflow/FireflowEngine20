create database `fireflowdemo` default character set utf8 collate utf8_general_ci;

create user `fireflow`@`localhost` identified by '123456';

grant all privileges on `fireflowdemo`.* to fireflow;