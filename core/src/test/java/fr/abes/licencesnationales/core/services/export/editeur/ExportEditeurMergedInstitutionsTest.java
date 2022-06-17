package fr.abes.licencesnationales.core.services.export.editeur;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.converter.ExportConverter;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurFusionDto;
import fr.abes.licencesnationales.core.dto.export.ExportEtablissementEditeurScissionDto;
import fr.abes.licencesnationales.core.entities.DateEnvoiEditeurEntity;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementDiviseEventEntity;
import fr.abes.licencesnationales.core.entities.etablissement.event.EtablissementFusionneEventEntity;
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

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ExportEditeurMergedInstitutions.class, ObjectMapper.class, ExportConverter.class, UtilsMapper.class})
public class ExportEditeurMergedInstitutionsTest {
    @Autowired
    private ExportEditeurMergedInstitutions service;

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

    @BeforeEach
    void init() throws IpException {
        TypeEtablissementEntity type1 = new TypeEtablissementEntity(1, "testType");

        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        etabIn1 = new EtablissementEntity(2, "testNom2", "111111111", type1, "12345", contact1);
        etabIn2 = new EtablissementEntity(3, "testNom3", "222222222", type1, "654321", contact1);
        etabIn3 = new EtablissementEntity(4, "testNom4", "333333333", type1, "987654", contact1);

        listeEtabs = new ArrayList<>();
        listeEtabs.add(etabIn1);
        listeEtabs.add(etabIn2);
        listeEtabs.add(etabIn3);
    }

    /*@Test
    @DisplayName("test récupération établissement fusionnés depuis la dernière exécution")
    void testExportEditeurMergedInstitutions() throws JsonProcessingException {
        Mockito.when(etablissementService.getAllEtabEditeur(Mockito.any())).thenReturn(listeEtabs);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, 0, 1);
        DateEnvoiEditeurEntity dateEnvoi = new DateEnvoiEditeurEntity(calendar.getTime());
        Mockito.when(dateEnvoiEditeurRepository.findTopByOrderByDateEnvoiDesc()).thenReturn(Optional.of(dateEnvoi));

        calendar.set(2021, 0, 1);
        Mockito.when(eventService.getDateFusionEtab(etabIn1)).thenReturn(calendar.getTime());
        calendar.set(2022, 1, 1);
        Mockito.when(eventService.getDateFusionEtab(etabIn2)).thenReturn(calendar.getTime());
        calendar.set(2022, 1, 2);
        Mockito.when(eventService.getDateFusionEtab(etabIn3)).thenReturn(calendar.getTime());

        Set<String> setSirenFusionnes1 = new HashSet<>();
        setSirenFusionnes1.add("888888888");
        setSirenFusionnes1.add("777777777");
        Set<String> setSirenFusionnes2 = new HashSet<>();
        setSirenFusionnes2.add("999999999");
        setSirenFusionnes2.add("666666666");
        Mockito.when(eventService.getEtabFusionEvent("222222222")).thenReturn(new EtablissementFusionneEventEntity(this,"222222222", setSirenFusionnes1));
        Mockito.when(eventService.getEtabFusionEvent("333333333")).thenReturn(new EtablissementFusionneEventEntity(this, "333333333", setSirenFusionnes2));

        List<Integer> listIds = new ArrayList<>();
        listIds.add(2);
        listIds.add(3);
        listIds.add(4);
        List<ExportEtablissementEditeurFusionDto> result = service.getItems(listIds);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("654321", result.get(0).getIdEtablissement());
        Assertions.assertEquals("987654", result.get(1).getIdEtablissement());
        Iterator it1 = result.get(0).getSirensFusionnes().iterator();
        Assertions.assertEquals("777777777", it1.next());
        Assertions.assertEquals("888888888", it1.next());
        Iterator it2 = result.get(1).getSirensFusionnes().iterator();
        Assertions.assertEquals("666666666", it2.next());
        Assertions.assertEquals("999999999", it2.next());
    }*/
}
