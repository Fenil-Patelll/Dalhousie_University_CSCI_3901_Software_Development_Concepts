use fenilp;

create table distributionhub(
 hubIdentifier varchar(10),
 x_coordinates varchar(15),
 y_coordinates varchar(15),
 servicedAreas varchar(6),
 primary key(hubIdentifier,servicedAreas)

);

create table PostalCodes(
   postalCode varchar(6),
   population int(11),
   area int(11),
   primary key (postalCode)
);

create table hubdamage(
    hubIdentifier varchar(10),
    repairEstimate float,
    inService boolean,
    primary key (hubIdentifier)
);

create table hubrepair(
   hubIdentifier varchar(10),
   employeeId varchar(8),
   repairTime float,
   inservice boolean
);

-- Note - for the better output please use input method of java code for the queries insertion, it is adviced to not to use below static queries for  table data
-- as some of the queries depended on the dynamic varibale of java code

drop table distributionhub;
drop table PostalCodes;
drop table hubdamage;
drop table hubrepair;

Insert into PostalCodes(postalCode,population,area) values("B3H 2K9", 4550, 121);
Insert into PostalCodes(postalCode,population,area) values("B3I 2K9",3000,152);
Insert into PostalCodes(postalCode,population,area) values("B3J 2K9",6500, 144);
Insert into PostalCodes(postalCode,population,area) values("B3K 2K9",4100, 79);
Insert into PostalCodes(postalCode,population,area) values("B3L 2K9",5000,134);
Insert into PostalCodes(postalCode,population,area) values("B3M 2K9",7000, 91);

Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("AB1","2","4","B3H 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("AB1","2","4","B3I 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("AB1","2","4","B3J 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("AB1","2","4","B3K 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("MN3","7","2","B3I 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("MN3","7","2","B3K 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("MN3","7","2","B3L 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("PQ2","5","9","B3H 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("PQ2","5","9","B3K 2K9");
Insert into distributionhub(hubIdentifier,x_coordinates,y_coordinates,servicedAreas) values ("PQ2","5","9","B3M 2K9");

Insert into hubdamage(hubIdentifier,repairEstimate,inService) values("AB1", 10,false);
Insert into hubdamage(hubIdentifier,repairEstimate,inService) values("MN3",6,false);
Insert into hubdamage(hubIdentifier,repairEstimate,inService) values("PQ2",8,false);

-- hubrepair query might wont work as expected, because I have put the if condition in hubrepair function of java code and the query is
-- dependent(change) on that if condition, the query I have put here is static, so for the perfect execution of the this query,
-- please call the input methods of the java code by providing data into those Input funtions
Insert into hubrepair(hubIdentifier,employeeId,repairTime,inService) values("AB1","E1",3,false);
Insert into hubrepair(hubIdentifier,employeeId,repairTime,inService) values("AB1","E1",4,false);
Insert into hubrepair(hubIdentifier,employeeId,repairTime,inService) values("AB1","E1",3,false);

select * from distributionhub;
select * from PostalCodes;
select * from hubdamage;
select * from hubrepair;













