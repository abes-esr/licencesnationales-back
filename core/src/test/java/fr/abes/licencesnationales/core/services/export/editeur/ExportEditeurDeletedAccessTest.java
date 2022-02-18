package fr.abes.licencesnationales.core.services.export.editeur;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.ExportConverter;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurDto;
import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.repository.DateEnvoiEditeurRepository;
import fr.abes.licencesnationales.core.services.EtablissementService;
import fr.abes.licencesnationales.core.services.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ExportEditeurDeletedAccess.class, ObjectMapper.class, ExportConverter.class, UtilsMapper.class})
public class ExportEditeurDeletedAccessTest {
    @Autowired
    private ExportEditeurDeletedAccess service;

    @Autowired
    private UtilsMapper mapper;

    @MockBean
    private EtablissementService etablissementService;

    @MockBean
    private DateEnvoiEditeurRepository dateEnvoiEditeurRepository;

    @MockBean
    private EventService eventService;

    private List<EtablissementEntity> listeEtabs;
    private EtablissementEntity etabIn1;
    private EtablissementEntity etabIn2;
    private EtablissementEntity etabIn3;

    private IpEntity ip1;
    private IpEntity ip2;
    private IpEntity ip3;
    private IpEntity ip4;
    private IpEntity ip5;

    @BeforeEach
    void init() throws IpException {
        TypeEtablissementEntity type1 = new TypeEtablissementEntity(1, "testType");

        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        etabIn1 = new EtablissementEntity(2, "testNom2", "111111111", type1, "12345", contact1);
        etabIn2 = new EtablissementEntity(3, "testNom3", "222222222", type1, "654321", contact1);
        etabIn3 = new EtablissementEntity(4, "testNom4", "333333333", type1, "987654", contact1);

        StatutIpEntity statut = new StatutIpEntity(1, "validé");
        ip1 = new IpV4(1, "1.1.1.1", "test", statut);
        ip2 = new IpV4(2, "2.2.2.2", "test", statut);
        ip3 = new IpV4(3, "3.3.3.3", "test", statut);
        ip4 = new IpV4(4, "4.4.4.4", "test", statut);
        ip5 = new IpV4(5, "5.5.5.5", "test", statut);

        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);
        etabIn1.ajouterIp(ip3);
        etabIn3.ajouterIp(ip4);
        etabIn3.ajouterIp(ip5);

        listeEtabs = new ArrayList<>();
        listeEtabs.add(etabIn1);
        listeEtabs.add(etabIn2);
        listeEtabs.add(etabIn3);
    }

    @Test
    @DisplayName("test récupération établissement avec au moins une IP validée depuis la dernière exécution")
    void testExportEditeurDeletedAccess() {
        Mockito.when(etablissementService.getAllEtabEditeur(Mockito.any())).thenReturn(listeEtabs);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, 0, 1);
        DateEnvoiEditeurEntity dateEnvoi = new DateEnvoiEditeurEntity(calendar.getTime());
        Mockito.when(dateEnvoiEditeurRepository.findTopByOrderByDateEnvoiDesc()).thenReturn(Optional.of(dateEnvoi));

        calendar.set(2021, 0, 1);
        Mockito.when(eventService.getDateValidationIp(ip1)).thenReturn(calendar.getTime());
        calendar.set(2021, 0, 1);
        Mockito.when(eventService.getDateValidationIp(ip2)).thenReturn(calendar.getTime());
        calendar.set(2022, 1, 2);
        Mockito.when(eventService.getDateValidationIp(ip3)).thenReturn(calendar.getTime());
        calendar.set(2022, 1, 1);
        Mockito.when(eventService.getDateValidationIp(ip4)).thenReturn(calendar.getTime());
        calendar.set(2021, 0, 1);
        Mockito.when(eventService.getDateValidationIp(ip5)).thenReturn(calendar.getTime());

        calendar.set(2022, 1, 2);
        Mockito.when(eventService.getDateSuppressionIp(ip1)).thenReturn(calendar.getTime());
        calendar.set(2021, 3, 1);
        Mockito.when(eventService.getDateSuppressionIp(ip2)).thenReturn(calendar.getTime());
        calendar.set(2022, 1, 2);
        Mockito.when(eventService.getDateSuppressionIp(ip5)).thenReturn(calendar.getTime());

        List<Integer> listIds = new ArrayList<>();
        listIds.add(2);
        listIds.add(3);
        listIds.add(4);
        List<ExportEtablissementEditeurDto> result = service.getItems(listIds);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("12345", result.get(0).getIdEtablissement());
        Assertions.assertEquals("987654", result.get(1).getIdEtablissement());
        Assertions.assertEquals(1, result.get(0).getListeAcces().size());
        Assertions.assertEquals(1, result.get(1).getListeAcces().size());
        Assertions.assertEquals("1.1.1.1", result.get(0).getListeAcces().get(0));
        Assertions.assertEquals("5.5.5.5", result.get(1).getListeAcces().get(0));
    }
}
