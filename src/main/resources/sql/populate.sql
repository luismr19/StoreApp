insert into article (LINK) values('http://newlink.com');

insert into category(NAME) values('protein');

insert into brand(FULLNAME,SHORTNAME) values('Optimum Nutrition','ON');

insert into type(NAME) values ('Muscle growth');

insert into product(PRODUCT_ID,DESCRIPTION,ENABLED,EXISTENCE,NAME,PRICE,TYPE_ID)
VALUES (1,'whey protein isolate',1,15,'Gold standard',50.5,1);

insert into product_category values(1,1,1);

insert into purchase_order(PURCHASE_TIME,TOTAL,TRACK_NUMBER,ADDRESS,OWNER) 
values (STR_TO_DATE('04-03-2016','%d-%m-%Y'),50.5,'R57W1234',1,1);

insert into order_detail(QUANTITY,PRODUCT_ID,ORDER_ID) values (1,1,1);

insert into benefit(DISCOUNT,POINTS,PURCHASE_ORDER) values(0.0,0,1);

insert into benefit_product(BENEFIT_ID,PRODUCT_ID)value(1,1);