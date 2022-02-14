package fr.abes.licencesnationales.core.services;

import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.dto.NotificationAdminDto;
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

    @DisplayName("test récupération établissement non validés")
    @Test
    void testGetEtabNouveau() {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);
        etabIn1.setValide(false);
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "111111111", type, "12345", contact);
        etabIn2.setValide(true);
        EtablissementEntity etabIn3 = new EtablissementEntity(3, "testNom3", "333333333", type, "12345", contact);
        etabIn3.setValide(false);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);
        listIn.add(etabIn3);

        List<NotificationAdminDto> notifs = service.getEtabNonValides(listIn);
        Assertions.assertEquals(2, notifs.size());
        Assertions.assertEquals("000000000", notifs.get(0).getSiren());
        Assertions.assertEquals("testNom", notifs.get(0).getNomEtab());
        Assertions.assertEquals("Nouvel établissement", notifs.get(0).getTypeNotif());
        Assertions.assertEquals("333333333", notifs.get(1).getSiren());
        Assertions.assertEquals("testNom3", notifs.get(1).getNomEtab());
        Assertions.assertEquals("Nouvel établissement", notifs.get(1).getTypeNotif());
    }

    @DisplayName("test récupération établissement avec IP en validation")
    @Test
    void testGetEtabIpEnValidation() throws IpException {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "000000000", type, "12345", contact);
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "111111111", type, "12345", contact);
        EtablissementEntity etabIn3 = new EtablissementEntity(3, "testNom3", "333333333", type, "12345", contact);

        StatutIpEntity statutAValider = new StatutIpEntity(Constant.STATUT_IP_NOUVELLE, "En validation");
        StatutIpEntity statutValide = new StatutIpEntity(Constant.STATUT_IP_ATTESTATION, "Attestation demandée");
        Date dateJour = new Date();
        IpEntity ip1 = new IpV4(1, "1.1.1.1", "test", statutValide);
        ip1.setDateCreation(dateJour);
        IpEntity ip2 = new IpV4(2, "2.2.2.2", "test", statutValide);
        ip2.setDateCreation(dateJour);
        IpEntity ip3 = new IpV4(3, "3.3.3.3", "test", statutAValider);
        ip3.setDateModification(dateJour);
        IpEntity ip4 = new IpV4(4, "4.4.4.4", "test", statutAValider);
        ip4.setDateCreation(dateJour);
        IpEntity ip5 = new IpV4(5, "5.5.5.5", "test", statutValide);
        ip5.setDateCreation(dateJour);
        IpEntity ip6 = new IpV4(6, "6.6.6.6", "test", statutAValider);
        ip6.setDateCreation(dateJour);

        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);
        etabIn2.ajouterIp(ip3);
        etabIn3.ajouterIp(ip4);
        etabIn3.ajouterIp(ip5);
        etabIn3.ajouterIp(ip6);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);
        listIn.add(etabIn3);

        List<NotificationAdminDto> notifs = service.getEtabIpEnValidation(listIn);
        Assertions.assertEquals(2, notifs.size());
        Assertions.assertEquals("111111111", notifs.get(0).getSiren());
        Assertions.assertEquals("testNom2", notifs.get(0).getNomEtab());
        Assertions.assertEquals("Nouvelle IP", notifs.get(0).getTypeNotif());
        Assertions.assertEquals("333333333", notifs.get(1).getSiren());
        Assertions.assertEquals("testNom3", notifs.get(1).getNomEtab());
        Assertions.assertEquals("Nouvelle IP", notifs.get(1).getTypeNotif());
    }

    @DisplayName("test récupération établissement ayant supprimé toutes ses IP depuis le dernier envoi éditeur")
    @Test
    void testGetEtabIpSupprimee() throws IpException {
        TypeEtablissementEntity type = new TypeEtablissementEntity(1, "testType");
        ContactEntity contact = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        EtablissementEntity etabIn1 = new EtablissementEntity(1, "testNom", "111111111", type, "12345", contact);
        EtablissementEntity etabIn2 = new EtablissementEntity(2, "testNom2", "222222222", type, "12345", contact);
        EtablissementEntity etabIn3 = new EtablissementEntity(3, "testNom3", "333333333", type, "12345", contact);

        List<EtablissementEntity> listIn = new ArrayList<>();
        listIn.add(etabIn1);
        listIn.add(etabIn2);
        listIn.add(etabIn3);

        Mockito.when(eventService.getLastDateSuppressionIpEtab(etabIn1)).thenReturn(Calendar.getInstance().getTime());
        Mockito.when(eventService.getLastDateSuppressionIpEtab(etabIn2)).thenReturn(null);
        Calendar enDehors = Calendar.getInstance();
        enDehors.set(2022, 0, 1);
        Mockito.when(eventService.getLastDateSuppressionIpEtab(etabIn3)).thenReturn(enDehors.getTime());

        List<NotificationAdminDto> notif = service.getEtabIpSupprimee(listIn);

        Assertions.assertEquals(1, notif.size());
        Assertions.assertEquals("111111111", notif.get(0).getSiren());
        Assertions.assertEquals("testNom1", notif.get(0).getNomEtab());
        Assertions.assertEquals("Suppression IP depuis dernier envoi", notif.get(0).getTypeNotif());
    }
}