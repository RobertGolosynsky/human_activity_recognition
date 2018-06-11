INSERT INTO `role` VALUES (1,'USER'),(2,'MODERATOR'),(3,'ADMIN');

INSERT INTO `user` VALUES (1,'user@user.us','User','Userovich','2017-11-12 16:55:05','$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC',1),
(2,'expired@expired.ex','Expired','Expiredovich','2050-01-01 16:55:05','$2a$10$PZ.A0IuNG958aHnKDzILyeD9k44EOi1Ny0VlAn.ygrGcgmVcg8PRK',1);

INSERT INTO `moderator` VALUES (1,'admin@admin.ad','Admin','Adminovich','2017-11-12 16:55:05','$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi',3);
INSERT INTO `moderator` VALUES (2,'moderator@moder.mo','Moderator','Moderatovich','2017-11-12 16:55:05','$2a$10$7N4P95cdnVO6yZgWcYFQ6O3NpCUDxNBOyE5Fx6S2NhP1DgGxSDtHG',2);
INSERT INTO `moderator` VALUES (3,'expired@exp.ex','Moder','Expo','2050-11-12 16:55:05','$2a$10$PZ.A0IuNG958aHnKDzILyeD9k44EOi1Ny0VlAn.ygrGcgmVcg8PRK',3);


