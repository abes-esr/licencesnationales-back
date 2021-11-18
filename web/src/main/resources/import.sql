insert into Statut(id_statut, libelle, dtype) values(1, 'Nouvelle IP', 'ip');
insert into Statut(id_statut, libelle, dtype) values(2, 'Attestation à envoyer', 'ip');
insert into Statut(id_statut, libelle, dtype) values(3, 'IP Validée', 'ip');

insert into type_etablissement(id, libelle) values(1, 'Universités, grandes écoles, écoles de formation spécialisées');
insert into type_etablissement(id, libelle) values(2, 'CHR-CHU');
insert into type_etablissement(id, libelle) values(3, 'Etablissements de santé (autres que CHR-CHU)');
insert into type_etablissement(id, libelle) values(4, 'Ecoles françaises à l étranger');
insert into type_etablissement(id, libelle) values(5, 'Etablissements publics administratifs');
insert into type_etablissement(id, libelle) values(6, 'Organismes de recherche');
insert into type_etablissement(id, libelle) values(7, 'Etablissements publics de coopération scientifique');
insert into type_etablissement(id, libelle) values(8, 'Etablissements publics de coopération culturelle');
insert into type_etablissement(id, libelle) values(9, 'Etablissements publics à caractère industriel et commercial');
insert into type_etablissement(id, libelle) values(10, 'Fondation reconnues d utilité publique');
insert into type_etablissement(id, libelle) values(11, 'GIP');
insert into type_etablissement(id, libelle) values(12, 'Réseau Latitude France');
insert into type_etablissement(id, libelle) values(13, 'Bibliothèques de lecture publique.');

Insert into CONTACT (ID, ADRESSE,BOITE_POSTALE,CEDEX,CODE_POSTAL,MAIL,MOT_DE_PASSE,NOM,PRENOM,TELEPHONE,VILLE,ROLE) values (1, 'Adminee',null,null,'34000','tcn@abes.fr','$2a$10$8iU5R4Xw6GuBpH3U8jiO1.e.5DGbMsc68mOm86899NkAO.o7b0/Ki','Admin','Admin','0000000000','Admin','admin');
Insert into CONTACT (ID, ADRESSE,BOITE_POSTALE,CEDEX,CODE_POSTAL,MAIL,MOT_DE_PASSE,NOM,PRENOM,TELEPHONE,VILLE,ROLE) values (2, 'Test Raluca',null,null,'34000','pierrot@abes.fr','$2a$10$Eq/IJHfRfuezZ6ESpacZOugUGvAUjm6thkE6GacaaA/DMHUJDsI4m','Test Raluca','Test Raluca','0633333333','Montpellier','admin');


Insert into ETABLISSEMENT (ID_ABES,DATE_CREATION,NOM,SIREN,CONTACT_ID,VALIDE,REF_TYPE_ETABLISSEMENT) values ('iCU9dGkJk4QycfJ71R98a51L',to_timestamp('07/10/21 14:34:44,033000000','DD/MM/RR HH24:MI:SSXFF'),'Admin','000000000','1',true,'2');
Insert into ETABLISSEMENT (ID_ABES,DATE_CREATION,NOM,SIREN,CONTACT_ID,VALIDE,REF_TYPE_ETABLISSEMENT) values ('3TKfuZg8X4V6djY8sZ8gNgv0',to_timestamp('08/11/21 12:35:11,326000000','DD/MM/RR HH24:MI:SSXFF'),'Université Gustave Eiffel','130026123','2',true,'1');
