package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementCreeEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementModifieEventEntity;
import fr.abes.licencesnationales.core.entities.ip.event.IpEventEntity;
import fr.abes.licencesnationales.core.exception.UnknownEtablissementException;
import fr.abes.licencesnationales.core.repository.editeur.EditeurEventRepository;
import fr.abes.licencesnationales.core.repository.etablissement.EtablissementEventRepository;
import fr.abes.licencesnationales.core.repository.ip.IpEventRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {EventService.class})
public class EventServiceTest {
    @Autowired
    private EventService service;
    @MockBean
    private IpEventRepository ipRepository;
    @MockBean
    private EtablissementEventRepository etablissementRepository;
    @MockBean
    private EditeurEventRepository editeurRepository;
    @MockBean
    private ObjectMapper mapper;

    @DisplayName("test récupération date suppression dernière IP d'un etablissement")
    @Test
    void testGetLastDateSuppressionIpEtab() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/mm/yyyy");
        IpEventEntity ip1 = new IpEventEntity(this, 1, "1.1.1.1");
        ip1.setSiren("111111111");
        ip1.setDateCreationEvent(format.parse("10/02/2020"));

        IpEventEntity ip2 = new IpEventEntity(this, 2, "2.2.2.2");
        ip2.setSiren("111111111");
        ip2.setDateCreationEvent(format.parse("15/03/2021"));

        List<IpEventEntity> listeIp = new ArrayList<>();
        listeIp.add(ip1);
        listeIp.add(ip2);

        Mockito.when(ipRepository.getIpSupprimeBySiren("111111111")).thenReturn(listeIp);

        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        Date dateLastSuppression = service.getLastDateSuppressionIpEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact));

        Assertions.assertEquals("15/03/2021", format.format(dateLastSuppression));
    }

    @DisplayName("test récupération date suppression dernière IP d'un établissement : pas d'IP")
    @Test
    void testGetLastDateSuppressionIpEtabNoIp(){
        Mockito.when(ipRepository.getIpSupprimeBySiren("111111111")).thenReturn(new ArrayList<>());
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        Date dateLastSuppression = service.getLastDateSuppressionIpEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact));

        Assertions.assertEquals(null, dateLastSuppression);
    }

    @DisplayName("test récupération date de création d'un établissement")
    @Test
    void testGetDateCreationEtab() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/mm/yyyy");
        EtablissementEventEntity etab = new EtablissementCreeEventEntity(this);
        etab.setDateCreationEvent(format.parse("10/02/2020"));
        Mockito.when(etablissementRepository.getDateCreationEtab("111111111")).thenReturn(List.of(etab));

        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        Assertions.assertEquals("10/02/2020", format.format(service.getDateCreationEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact).getSiren())));
    }

    @DisplayName("test récupération date de création d'un établissement : établissement inconnu")
    @Test
    void testGetDateCreationEtabUnknown() {
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        Exception ex = Assertions.assertThrows(UnknownEtablissementException.class, () -> service.getDateCreationEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact).getSiren()));
        Assertions.assertEquals(String.format(Constant.ERROR_ETAB_EXISTE_PAS,"111111111"), ex.getMessage());
    }

    @DisplayName("test récupération de la dernière date de modification d'un établissement")
    @Test
    void testGetLastDateModificationEtab() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/mm/yyyy");
        EtablissementEventEntity etab1 = new EtablissementModifieEventEntity(this,"111111111");
        etab1.setDateCreationEvent(format.parse("10/02/2020"));

        EtablissementEventEntity etab2 = new EtablissementModifieEventEntity(this,"111111111");
        etab2.setDateCreationEvent(format.parse("15/03/2021"));

        List<EtablissementEventEntity> listeEtab = new ArrayList<>();
        listeEtab.add(etab1);
        listeEtab.add(etab2);

        Mockito.when(etablissementRepository.getLastModicationEtab("111111111")).thenReturn(listeEtab);

        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        Date dateDerniereModif = service.getLastDateModificationEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact).getSiren());
        Assertions.assertEquals("15/03/2021", format.format(dateDerniereModif));
    }

    @DisplayName("test récupération dernière date de modification d'un établissement : pas de modification")
    @Test
    void testGetLastDateModificationEtabNoModif(){
        Mockito.when(etablissementRepository.getLastModicationEtab("111111111")).thenReturn(new ArrayList<>());
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        Date dateLastModification = service.getLastDateModificationEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact).getSiren());

        Assertions.assertEquals(null, dateLastModification);
    }

    @DisplayName("test getHistoEtab avec remontée vers ancien SIREN")
    @Test
    void testGetHistoEtabRemonteeAncienSiren() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        // Événement de création avec l'ancien SIREN (111111111)
        EtablissementEventEntity eventCree = new EtablissementCreeEventEntity(this);
        eventCree.setId(1);
        eventCree.setSiren("111111111");
        eventCree.setDateCreationEvent(format.parse("10/01/2020"));

        // Événement de modification avec le nouveau SIREN (222222222) et ancienSiren (111111111)
        EtablissementModifieEventEntity eventModif = new EtablissementModifieEventEntity(this, "222222222", "111111111");
        eventModif.setId(2);
        eventModif.setDateCreationEvent(format.parse("15/06/2020"));

        Mockito.when(etablissementRepository.findBySiren("222222222")).thenReturn(List.of(eventModif));
        Mockito.when(etablissementRepository.findBySiren("111111111")).thenReturn(List.of(eventCree));

        List<EtablissementEventEntity> histo = service.getHistoEtab("222222222");

        Assertions.assertEquals(2, histo.size());
        Assertions.assertEquals(1, histo.get(0).getId());
        Assertions.assertEquals(2, histo.get(1).getId());
    }

    @DisplayName("test getHistoEtab avec descente vers nouveau SIREN")
    @Test
    void testGetHistoEtabDescenteNouveauSiren() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        // Événement de création pour le SIREN d'origine (111111111)
        EtablissementEventEntity eventCree = new EtablissementCreeEventEntity(this);
        eventCree.setId(1);
        eventCree.setSiren("111111111");
        eventCree.setDateCreationEvent(format.parse("10/01/2020"));

        // Événement de modification ayant changé 111111111 vers 222222222
        EtablissementModifieEventEntity eventModif = new EtablissementModifieEventEntity(this, "222222222", "111111111");
        eventModif.setId(2);
        eventModif.setDateCreationEvent(format.parse("15/06/2020"));

        Mockito.when(etablissementRepository.findBySiren("111111111")).thenReturn(List.of(eventCree));
        Mockito.when(etablissementRepository.findByAncienSiren("111111111")).thenReturn(List.of(eventModif));
        Mockito.when(etablissementRepository.findBySiren("222222222")).thenReturn(List.of(eventModif));

        List<EtablissementEventEntity> histo = service.getHistoEtab("111111111");

        Assertions.assertEquals(2, histo.size());
        Assertions.assertEquals(1, histo.get(0).getId());
        Assertions.assertEquals(2, histo.get(1).getId());
    }

    @DisplayName("test getHistoEtab avec chaîne multi-SIREN (A -> B -> C)")
    @Test
    void testGetHistoEtabChaineMultiSiren() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        EtablissementEventEntity eventA = new EtablissementCreeEventEntity(this);
        eventA.setId(1);
        eventA.setSiren("111111111");
        eventA.setDateCreationEvent(format.parse("01/01/2020"));

        EtablissementModifieEventEntity eventB = new EtablissementModifieEventEntity(this, "222222222", "111111111");
        eventB.setId(2);
        eventB.setDateCreationEvent(format.parse("01/06/2020"));

        EtablissementModifieEventEntity eventC = new EtablissementModifieEventEntity(this, "333333333", "222222222");
        eventC.setId(3);
        eventC.setDateCreationEvent(format.parse("01/12/2020"));

        // Appel depuis le SIREN intermédiaire B (222222222)
        Mockito.when(etablissementRepository.findBySiren("222222222")).thenReturn(List.of(eventB));
        Mockito.when(etablissementRepository.findBySiren("111111111")).thenReturn(List.of(eventA));
        Mockito.when(etablissementRepository.findBySiren("333333333")).thenReturn(List.of(eventC));

        Mockito.when(etablissementRepository.findByAncienSiren("222222222")).thenReturn(List.of(eventC));
        Mockito.when(etablissementRepository.findByAncienSiren("111111111")).thenReturn(List.of(eventB));

        List<EtablissementEventEntity> histo = service.getHistoEtab("222222222");

        Assertions.assertEquals(3, histo.size());
        Assertions.assertEquals(1, histo.get(0).getId());
        Assertions.assertEquals(2, histo.get(1).getId());
        Assertions.assertEquals(3, histo.get(2).getId());
    }

    @DisplayName("test getHistoAllEtab sort tous les events depuis la création des établissements concernés")
    @Test
    void testGetHistoAllEtabDepuisCreation() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDebut = format.parse("01/05/2020");
        Date dateFin = format.parse("30/06/2020");

        // Événement de création hors de la période (antérieur)
        EtablissementEventEntity eventCree = new EtablissementCreeEventEntity(this);
        eventCree.setId(1);
        eventCree.setSiren("111111111");
        eventCree.setDateCreationEvent(format.parse("10/01/2020"));

        // Événement dans la période : modification vers 222222222 avec ancienSiren 111111111
        EtablissementModifieEventEntity eventModif = new EtablissementModifieEventEntity(this, "222222222", "111111111");
        eventModif.setId(2);
        eventModif.setDateCreationEvent(format.parse("15/06/2020"));

        // findBetweenDates ne renvoie que l'événement présent dans la période
        Mockito.when(etablissementRepository.findBetweenDates(dateDebut, dateFin)).thenReturn(List.of(eventModif));

        // getHistoEtab découvre l'historique complet pour les SIRENs concernés
        Mockito.when(etablissementRepository.findBySiren("222222222")).thenReturn(List.of(eventModif));
        Mockito.when(etablissementRepository.findBySiren("111111111")).thenReturn(List.of(eventCree));

        List<EtablissementEventEntity> allEvents = service.getHistoAllEtab(dateDebut, dateFin);

        // Doit contenir tous les événements depuis la date de création de l'établissement, ordonnés
        Assertions.assertEquals(2, allEvents.size());
        Assertions.assertEquals(1, allEvents.get(0).getId());
        Assertions.assertEquals(2, allEvents.get(1).getId());
    }

    @DisplayName("test getDateCreationEtab se base sur la première mention du SIREN demandé (et non sur le premier event tout court)")
    @Test
    void testGetDateCreationEtabAvecChangementSiren() throws ParseException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        // Événement 1 : création initiale avec le SIREN A (111111111) le 10/02/2020
        EtablissementEventEntity event1 = new EtablissementCreeEventEntity(this);
        event1.setId(1);
        event1.setSiren("111111111");
        event1.setDateCreationEvent(format.parse("10/02/2020"));

        // Événement 2 : modification vers le SIREN B (222222222) le 15/06/2021
        EtablissementModifieEventEntity event2 = new EtablissementModifieEventEntity(this, "222222222", "111111111");
        event2.setId(2);
        event2.setDateCreationEvent(format.parse("15/06/2021"));

        Mockito.when(etablissementRepository.findBySiren("222222222")).thenReturn(List.of(event2));
        Mockito.when(etablissementRepository.findBySiren("111111111")).thenReturn(List.of(event1));

        // Pour le SIREN B (222222222) : sa date de création doit être la date de sa première mention (15/06/2021), et non celle de A (10/02/2020)
        Date dateCreationB = service.getDateCreationEtab("222222222");
        Assertions.assertEquals("15/06/2021", format.format(dateCreationB));

        // Pour le SIREN A (111111111) : sa date de création est bien celle de sa première mention (10/02/2020)
        Mockito.when(etablissementRepository.findByAncienSiren("111111111")).thenReturn(List.of(event2));
        Date dateCreationA = service.getDateCreationEtab("111111111");
        Assertions.assertEquals("10/02/2020", format.format(dateCreationA));
    }

}
