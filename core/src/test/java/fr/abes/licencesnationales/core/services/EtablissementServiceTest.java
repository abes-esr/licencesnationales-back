package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.exception.MailDoublonException;
import fr.abes.licencesnationales.core.exception.SirenExistException;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.StatutRepository;
import fr.abes.licencesnationales.core.repository.etablissement.ContactRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EtablissementService.class})
class EtablissementServiceTest {

    @Autowired
    private EtablissementService service;

    @MockBean
    private EtablissementRepository etablissementDao;

    @MockBean
    private ContactRepository contactEtablissementDao;

    @MockBean
    private EventService eventService;

    @MockBean
    private ReferenceService referenceService;

    @MockBean
    private StatutRepository statutRepository;

    @DisplayName("test getFirstBySiren success")
    @Test
    void testGetFirstBySirenSuccess() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);
        etabIn.setValide(false);
        Optional<EtablissementEntity> optionEtab = Optional.of(etabIn);
        Mockito.when(etablissementDao.getFirstBySiren("000000000")).thenReturn(optionEtab);
        EtablissementEntity etabOut = service.getFirstBySiren("000000000");
        Assertions.assertEquals(1, etabOut.getId().intValue());
        Assertions.assertEquals("testNom", etabOut.getNom());
        Assertions.assertEquals("000000000", etabOut.getSiren());
        Assertions.assertEquals("testType", etabOut.getTypeEtablissement().getLibelle());
        Assertions.assertEquals("12345", etabOut.getIdAbes());
        Assertions.assertEquals("nom", etabOut.getContact().getNom());
        Assertions.assertEquals("prenom", etabOut.getContact().getPrenom());
        Assertions.assertEquals("adresse", etabOut.getContact().getAdresse());
        Assertions.assertEquals("BP", etabOut.getContact().getBoitePostale());
        Assertions.assertEquals("CP", etabOut.getContact().getCodePostal());
        Assertions.assertEquals("ville", etabOut.getContact().getVille());
        Assertions.assertEquals("cedex", etabOut.getContact().getCedex());
        Assertions.assertEquals("telephone", etabOut.getContact().getTelephone());
        Assertions.assertEquals("mail@mail.com", etabOut.getContact().getMail());
        Assertions.assertEquals(false, etabOut.isValide());
        Assertions.assertNotNull(etabOut.getContact().getMotDePasse());
    }

    @DisplayName("test getFirstBySiren avec Exception")
    @Test
    void testGetFirstBySirenThrows() {
        Mockito.when(etablissementDao.getFirstBySiren("000000000")).thenThrow(new UnknownEtablissementException("Siren : 000000000"));
        Assertions.assertThrows(UnknownEtablissementException.class, ()->service.getFirstBySiren("000000000"));
    }

    @DisplayName("test save établissement sur création")
    @Test
    void saveCreation() throws MailDoublonException, SirenExistException {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn = new EtablissementEntity( "testNom", "000000000", type, "12345", contact);
        Mockito.when(etablissementDao.save(etabIn)).thenReturn(etabIn);

        service.save(etabIn);
    }

    @DisplayName("test save établissement sur modification")
    @Test
    void saveModification() throws MailDoublonException, SirenExistException {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn = new EtablissementEntity( 1, "testNom", "000000000", type, "12345", contact);
        Mockito.when(etablissementDao.save(etabIn)).thenReturn(etabIn);

        service.save(etabIn);
    }

    @DisplayName("test récupération établissement à supprimer pour batch (pas d'établissement sans IP)")
    @Test
    void testGetEtabASupprimerNoEtabWithIp() {
        Mockito.when(etablissementDao.getEtablissementEntityByIps_Empty()).thenReturn(new ArrayList<>());
        List<EtablissementEntity> listEtab = service.getEtabASupprimer();
        Assertions.assertEquals(0, listEtab.size());
    }

    @DisplayName("test récupération établissement à supprimer (via date de création etab)")
    @Test
    void testGetEtabASupprimerDateCreationEtab() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity( 1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity( 2, "testNom2", "000000000", type, "12345", contact2);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);

        Mockito.when(etablissementDao.getEtablissementEntityByIps_Empty()).thenReturn(listIn);
        Mockito.when(eventService.getLastDateSuppressionIpEtab(Mockito.any())).thenReturn(null);
        Calendar dateCreationEtab = new GregorianCalendar(2019, 1, 1);
        Mockito.when(eventService.getDateCreationEtab(etabIn1)).thenReturn(dateCreationEtab.getTime());
        Mockito.when(eventService.getDateCreationEtab(etabIn2)).thenReturn(Calendar.getInstance().getTime());

        List<EtablissementEntity> listeEtab = service.getEtabASupprimer();
        Assertions.assertEquals(1, listeEtab.size());
        Assertions.assertEquals("testNom", listeEtab.get(0).getNom());
    }

    @DisplayName("test récupération établissement à supprimer (via date de suppression dernière IP")
    @Test
    void testGetEtabASupprimerDateSuppressionDerniereIP() {
        Calendar oneYearAgo = new GregorianCalendar(2021, Calendar.NOVEMBER, 26);
        oneYearAgo.add(Calendar.YEAR, -1);
        service.setOneYearAgo(oneYearAgo);

        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact1);

        ContactEntity contact2 = new ContactEntity(2, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "000000000", type, "12345", contact2);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);
        Mockito.when(etablissementDao.getEtablissementEntityByIps_Empty()).thenReturn(listIn);
        Calendar dateCreationEtab1 = new GregorianCalendar(2019, Calendar.JANUARY, 1);
        Mockito.when(eventService.getLastDateSuppressionIpEtab(etabIn1)).thenReturn(dateCreationEtab1.getTime());
        Calendar dateCreationEtab2 = new GregorianCalendar(2021, Calendar.JANUARY, 25);
        Mockito.when(eventService.getLastDateSuppressionIpEtab(etabIn2)).thenReturn(dateCreationEtab2.getTime());

        List<EtablissementEntity> listEtab = service.getEtabASupprimer();
        Assertions.assertEquals(1, listEtab.size());
        Assertions.assertEquals("testNom", listEtab.get(0).getNom());
    }

    @DisplayName("test récupération établissements pour envoi éditeurs")
    @Test
    void testgetAllEtabEditeur() throws IpException {
        StatutIpEntity statutNouvelle = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "Nouvelle");
        StatutIpEntity statutValide = new StatutIpEntity(Constant.STATUT_IP_VALIDEE, "Validée");

        TypeEtablissementEntity type1 = new TypeEtablissementEntity(1, "testType");

        //Etablissement avec aucune IP Validée
        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type1, "12345", contact1);

        IpEntity ip1 = new IpV4("1.1.1.1", "test", statutNouvelle);
        IpEntity ip2 = new IpV4("2.2.2.2", "test", statutNouvelle);
        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);

        //Etablissement avec 2 Ip validée et une Ip Nouvelle
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "111111111", type1, "12345", contact1);

        IpEntity ip3 = new IpV4("3.3.3.3", "test", statutNouvelle);
        IpEntity ip4 = new IpV4("4.4.4.4", "test", statutValide);
        IpEntity ip5 = new IpV4("5.5.5.5", "test", statutValide);
        etabIn2.ajouterIp(ip3);
        etabIn2.ajouterIp(ip4);
        etabIn2.ajouterIp(ip5);

        //Etablissement avec une seule IP validée
        EtablissementEntity etabIn3 = new EtablissementEntity(3, "testNom3", "222222222", type1, "12345", contact1);

        IpEntity ip6 = new IpV4("6.6.6.6", "test", statutValide);
        etabIn3.ajouterIp(ip6);

        List<EtablissementEntity> listeEtabs = new ArrayList<>();
        listeEtabs.add(etabIn1);
        listeEtabs.add(etabIn2);
        listeEtabs.add(etabIn3);

        List<Integer> listeTypes = new ArrayList<>();
        listeTypes.add(1);

        List<TypeEtablissementEntity> listeTypesEtab = new ArrayList<>();
        listeTypesEtab.add(type1);

        Mockito.when(referenceService.findTypeEtabByIds(Mockito.any())).thenReturn(listeTypesEtab);
        Mockito.when(etablissementDao.findAllByValideAndTypeEtablissementIn(Mockito.anyBoolean(), Mockito.any())).thenReturn(listeEtabs);


        List<EtablissementEntity> listeResult = service.getAllEtabEditeur(listeTypes);

        Assertions.assertEquals(2, listeResult.size());
        Assertions.assertEquals("testNom2", listeResult.get(0).getNom());
        Assertions.assertEquals("testNom3", listeResult.get(1).getNom());
        Assertions.assertEquals(2, listeResult.get(0).getIps().size());
        Assertions.assertEquals(1, listeResult.get(1).getIps().size());
    }
}