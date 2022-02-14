package fr.abes.licencesnationales.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.mockito.InjectMocks;
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
import java.util.Optional;

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
        Mockito.when(etablissementRepository.getDateCreationEtab("111111111")).thenReturn(Optional.of(etab));

        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        Assertions.assertEquals("10/02/2020", format.format(service.getDateCreationEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact))));
    }

    @DisplayName("test récupération date de création d'un établissement : établissement inconnu")
    @Test
    void testGetDateCreationEtabUnknown() {
        Mockito.when(etablissementRepository.getDateCreationEtab("111111111")).thenReturn(Optional.empty());

        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        Exception ex = Assertions.assertThrows(UnknownEtablissementException.class, () -> service.getDateCreationEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact)));
        Assertions.assertEquals("Etablissement inconnu", ex.getLocalizedMessage());
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

        Date dateDerniereModif = service.getLastDateModificationEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact));
        Assertions.assertEquals("15/03/2021", format.format(dateDerniereModif));
    }

    @DisplayName("test récupération dernière date de modification d'un établissement : pas de modification")
    @Test
    void testGetLastDateModificationEtabNoModif(){
        Mockito.when(etablissementRepository.getLastModicationEtab("111111111")).thenReturn(new ArrayList<>());
        ContactEntity contact = new ContactEntity("nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");
        Date dateLastModification = service.getLastDateModificationEtab(new EtablissementEntity(1, "nomEtab", "111111111", new TypeEtablissementEntity(1, "Type"), "123456789", contact));

        Assertions.assertEquals(null, dateLastModification);
    }

}
