--liquibase formatted sql
--changeset author_liquibase:admin_insert_table_user
INSERT INTO TBUser
VALUES ('1', 'namdeptrai','Hoang Nhat Nam',null, '$2a$10$PXWfhZiL1ln4Uz/a.qyXSeGGF4Bx1RiCM4ZWAz6Vu0RiIDxTMgGIK', 'nam@test.com','2023-10-06','Hai Phong City', 'ROLE_USER', null , null)