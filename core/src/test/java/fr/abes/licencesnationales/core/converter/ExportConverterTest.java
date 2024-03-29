package fr.abes.licencesnationales.core.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEditeurDto;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurDto;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementUserDto;
import fr.abes.licencesnationales.core.dto.export.ExportIpDto;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactCommercialEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactEditeurEntity;
import fr.abes.licencesnationales.core.entities.contactediteur.ContactTechniqueEditeurEntity;
import fr.abes.licencesnationales.core.entities.editeur.EditeurEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.ip.IpV6;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {UtilsMapper.class, ObjectMapper.class, ExportConverter.class})
class ExportConverterTest {
    @Autowired
    private UtilsMapper mapper;

    @Test
    @DisplayName("Test convertisseur export établissement")
    void testMapperExportEtab() throws IpException {
        StatutIpEntity statut = new StatutIpEntity(3, "Validé");
        IpEntity ip1 = new IpV4(1, "1.1.1.1", "test", statut);
        IpEntity ip2 = new IpV4(2, "2.2.2.2", "test2", statut);

        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);

        etab.ajouterIp(ip1);
        etab.ajouterIp(ip2);

        ExportEtablissementUserDto dto = mapper.map(etab, ExportEtablissementUserDto.class);

        Assertions.assertEquals("123456", dto.getIdAbes());
        Assertions.assertEquals("123456789", dto.getSiren());
        Assertions.assertEquals("nomEtab", dto.getNom());
        Assertions.assertEquals("validé", dto.getTypeEtablissement());
        Assertions.assertEquals("adresse2 11111 ville2 BP2 cedex2", dto.getAdresse());
        Assertions.assertEquals("1111111111", dto.getTelephone());
        Assertions.assertEquals("nom2 prenom2", dto.getNomPrenomContact());
        Assertions.assertEquals("mail2@test.com", dto.getMailContact());
        Assertions.assertEquals(2, dto.getIps().size());
        Assertions.assertTrue(dto.getIps().contains("1.1.1.1"));
        Assertions.assertTrue(dto.getIps().contains("2.2.2.2"));
    }

    @Test
    @DisplayName("Test convertisseur export établissement sans BP")
    void testMapperExportEtabWithoutBP() {
        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse2", null, "11111", "ville2", "cedex2", "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);

        ExportEtablissementUserDto dto = mapper.map(etab, ExportEtablissementUserDto.class);

        Assertions.assertEquals("123456", dto.getIdAbes());
        Assertions.assertEquals("123456789", dto.getSiren());
        Assertions.assertEquals("nomEtab", dto.getNom());
        Assertions.assertEquals("validé", dto.getTypeEtablissement());
        Assertions.assertEquals("adresse2 11111 ville2 cedex2", dto.getAdresse());
        Assertions.assertEquals("1111111111", dto.getTelephone());
        Assertions.assertEquals("nom2 prenom2", dto.getNomPrenomContact());
        Assertions.assertEquals("mail2@test.com", dto.getMailContact());
        Assertions.assertEquals(0, dto.getIps().size());
    }

    @Test
    @DisplayName("Test convertisseur export établissement sans Cedex")
    void testMapperExportEtabWithoutCedex() {
        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse2", "BP2", "11111", "ville2", null, "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);

        ExportEtablissementUserDto dto = mapper.map(etab, ExportEtablissementUserDto.class);

        Assertions.assertEquals("123456", dto.getIdAbes());
        Assertions.assertEquals("123456789", dto.getSiren());
        Assertions.assertEquals("nomEtab", dto.getNom());
        Assertions.assertEquals("validé", dto.getTypeEtablissement());
        Assertions.assertEquals("adresse2 11111 ville2 BP2", dto.getAdresse());
        Assertions.assertEquals("1111111111", dto.getTelephone());
        Assertions.assertEquals("nom2 prenom2", dto.getNomPrenomContact());
        Assertions.assertEquals("mail2@test.com", dto.getMailContact());
        Assertions.assertEquals(0, dto.getIps().size());
    }

    @Test
    @DisplayName("Test convertisseur export éditeur sans contact")
    void testMapperExportEditeurSansContact() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1,"validé");
        Set<TypeEtablissementEntity> types = new HashSet<>();
        types.add(type);
        EditeurEntity editeur = new EditeurEntity(1,"nom","abes321654","ades@trc",types);

        ExportEditeurDto dto = mapper.map(editeur,ExportEditeurDto.class);
        Assertions.assertEquals("abes321654",dto.getId());
        Assertions.assertEquals("nom",dto.getNom());
        Assertions.assertEquals(0,dto.getContact().size());
        Assertions.assertEquals("ades@trc",dto.getAdresse());
    }
    @Test
    @DisplayName("Test convertisseur export éditeur Avec contact")
    void testMapperExportEditeurAvecContact() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1,"validé");
        Set<TypeEtablissementEntity> types = new HashSet<>();
        types.add(type);
        EditeurEntity editeur = new EditeurEntity(1,"nom","abes321654","ades@trc",types);
        ContactEditeurEntity contactCommercial = new ContactCommercialEditeurEntity(2,"nom2","prenom2","sqn@abes.fr");
        ContactEditeurEntity contactTechnique = new ContactTechniqueEditeurEntity(3,"nom3","prenom3","sqn@abes.fr");
        editeur.ajouterContact(contactTechnique);
        editeur.ajouterContact(contactCommercial);


        ExportEditeurDto dto = mapper.map(editeur,ExportEditeurDto.class);
        Assertions.assertEquals("abes321654",dto.getId());
        Assertions.assertEquals("nom",dto.getNom());
        Assertions.assertEquals(2,dto.getContact().size());
        Assertions.assertEquals("ades@trc",dto.getAdresse());
        Assertions.assertEquals("nom2 prenom2",dto.getContact().get(0).getNomPrenom());
        Assertions.assertEquals("sqn@abes.fr",dto.getContact().get(0).getMail());
        Assertions.assertEquals("Commercial",dto.getContact().get(0).getType());
        Assertions.assertEquals("nom3 prenom3",dto.getContact().get(1).getNomPrenom());
        Assertions.assertEquals("sqn@abes.fr",dto.getContact().get(1).getMail());
        Assertions.assertEquals("Technique",dto.getContact().get(1).getType());
    }

    @Test
    @DisplayName("Test convertisseur export IpV4")
    void testMapperExportIpv4() throws IpException {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        IpEntity ip = new IpV4(1,"192.162.0.1","test",new StatutIpEntity(1,"testType"));
        Date today = Calendar.getInstance().getTime();
        ip.setDateModification(today);
        ip.setDateCreation(today);

        ExportIpDto dto = mapper.map(ip, ExportIpDto.class);
        Assertions.assertEquals("192.162.0.1",dto.getIp());
        Assertions.assertEquals("IP V4",dto.getType());
        Assertions.assertEquals("test",dto.getCommentaire());
        Assertions.assertEquals("testType",dto.getStatut());
        Assertions.assertEquals(format.format(today),dto.getDateCreation());
        Assertions.assertEquals(format.format(today),dto.getDateModificationStatut());
    }

    @Test
    @DisplayName("Test convertisseur export range IpV4")
    void testMapperExportRangeIpv4() throws IpException {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        IpEntity ip = new IpV4(1,"192.162.0-0.1-10","test",new StatutIpEntity(1,"testType"));

        Date today = Calendar.getInstance().getTime();
        ip.setDateModification(today);
        ip.setDateCreation(today);

        ExportIpDto dto = mapper.map(ip, ExportIpDto.class);
        Assertions.assertEquals("192.162.0-0.1-10",dto.getIp());
        Assertions.assertEquals("Plage d'IP V4",dto.getType());
        Assertions.assertEquals("test",dto.getCommentaire());
        Assertions.assertEquals("testType",dto.getStatut());
        Assertions.assertEquals(format.format(today),dto.getDateCreation());
        Assertions.assertEquals(format.format(today),dto.getDateModificationStatut());
    }

    @Test
    @DisplayName("Test convertisseur export IpV6")
    void testMapperExportIpv6() throws IpException {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        IpEntity ip = new IpV6(1,"0000:0000:0000:0000:0000:0000:0000:0001","test",new StatutIpEntity(1,"testType"));
        Date today = Calendar.getInstance().getTime();
        ip.setDateModification(today);
        ip.setDateCreation(today);

        ExportIpDto dto = mapper.map(ip, ExportIpDto.class);
        Assertions.assertEquals("0000:0000:0000:0000:0000:0000:0000:0001",dto.getIp());
        Assertions.assertEquals("IP V6",dto.getType());
        Assertions.assertEquals("test",dto.getCommentaire());
        Assertions.assertEquals("testType",dto.getStatut());
        Assertions.assertEquals(format.format(today),dto.getDateCreation());
        Assertions.assertEquals(format.format(today),dto.getDateModificationStatut());
    }

    @Test
    @DisplayName("Test convertisseur export IpV6 range")
    void testMapperExportIpv6Range() throws IpException {
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        IpEntity ip = new IpV6(1,"0000:0000:0000:0000:0000-0000:0000:0000-0001:0001-00AA","test",new StatutIpEntity(1,"testType"));
        Date today = Calendar.getInstance().getTime();
        ip.setDateModification(today);
        ip.setDateCreation(today);

        ExportIpDto dto = mapper.map(ip, ExportIpDto.class);
        Assertions.assertEquals("0000:0000:0000:0000:0000:0000:0000-0001:0001-00AA",dto.getIp());
        Assertions.assertEquals("Plage d'IP V6",dto.getType());
        Assertions.assertEquals("test",dto.getCommentaire());
        Assertions.assertEquals("testType",dto.getStatut());
        Assertions.assertEquals(format.format(today),dto.getDateCreation());
        Assertions.assertEquals(format.format(today),dto.getDateModificationStatut());
    }

    @Test
    @DisplayName("Test convertisseur export éditeur établissement")
    void testMapperExportEtablissementEditeur() throws IpException {
        ContactEntity contact = new ContactEntity("nom2", "prenom2", "adresse", "BP2", "11111", "ville2", null, "1111111111", "mail2@test.com", "mdp2");
        EtablissementEntity etab = new EtablissementEntity(1, "nomEtab", "123456789", new TypeEtablissementEntity(3, "validé"), "123456", contact);
        IpEntity ip1 = new IpV4(1,"192.162.0-0.1-10","test",new StatutIpEntity(1,"Validé"));
        IpEntity ip2 = new IpV4(2,"192.162.0-0.1-20","test",new StatutIpEntity(1,"Validé"));
        etab.ajouterIp(ip1);
        etab.ajouterIp(ip2);


        ExportEtablissementEditeurDto dto = mapper.map(etab, ExportEtablissementEditeurDto.class);
        Assertions.assertEquals("123456", dto.getIdEtablissement());
        Assertions.assertEquals("123456789", dto.getSirenEtablissement());
        Assertions.assertEquals("nomEtab", dto.getNomEtablissement());
        Assertions.assertEquals("validé", dto.getTypeEtablissement());
        Assertions.assertEquals("adresse", dto.getAdresse());
        Assertions.assertEquals("BP2", dto.getBoitePostale());
        Assertions.assertEquals("11111", dto.getCodePostal());
        Assertions.assertEquals(null, dto.getCedex());
        Assertions.assertEquals("ville2", dto.getVille());
        Assertions.assertEquals("nom2 prenom2", dto.getNomContact());
        Assertions.assertEquals("mail2@test.com", dto.getMailContact());
        Assertions.assertEquals("1111111111", dto.getTelephoneContact());
        Assertions.assertEquals(2, dto.getListeAcces().size());
    }
}
