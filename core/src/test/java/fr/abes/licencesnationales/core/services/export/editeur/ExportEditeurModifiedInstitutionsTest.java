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
@SpringBootTest(classes = {ExportEditeurModifiedInstitutions.class, ObjectMapper.class, ExportConverter.class, UtilsMapper.class})
public class ExportEditeurModifiedInstitutionsTest {
    @Autowired
    private ExportEditeurModifiedInstitutions service;

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
    @BeforeEach
    void init() {
        TypeEtablissementEntity type1 = new TypeEtablissementEntity(1, "testType");

        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        etabIn1 = new EtablissementEntity(2, "testNom2", "111111111", type1, "12345", contact1);
        etabIn2 = new EtablissementEntity(3, "testNom3", "222222222", type1, "654321", contact1);

        listeEtabs = new ArrayList<>();
        listeEtabs.add(etabIn1);
        listeEtabs.add(etabIn2);
    }

   /* @Test
    @DisplayName("test récupération des établissements modifiés depuis dernière exécution")
    void testExportEditeurModifiedInstitutions() {
        Mockito.when(etablissementService.getAllEtabEditeur(Mockito.any())).thenReturn(listeEtabs);

        Calendar calendar = Calendar.getInstance();
        calendar.set(2022, 0, 1);
        DateEnvoiEditeurEntity dateEnvoi = new DateEnvoiEditeurEntity(calendar.getTime());
        Mockito.when(dateEnvoiEditeurRepository.findTopByOrderByDateEnvoiDesc()).thenReturn(Optional.of(dateEnvoi));

        calendar.set(2022, 1, 1);
        Mockito.when(eventService.getLastDateModificationEtab(etabIn1)).thenReturn(calendar.getTime());
        calendar.set(2021, 11, 1);
        Mockito.when(eventService.getLastDateModificationEtab(etabIn2)).thenReturn(calendar.getTime());

        List<Integer> listIds = new ArrayList<>();
        listIds.add(2);
        listIds.add(3);
        List<ExportEtablissementEditeurDto> result = service.getItems(listIds);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("12345", result.get(0).getIdEtablissement());
    }*/

   /* @Test
    @DisplayName("test récupération des établissement modifiés avec date de modification inconnue")
    void testExportEditeurModifiedInstitutionsWithEmptyDate() {
        //ce test vérifie juste que le programme ne plante pas si l'envoi éditeur n'a jamais été lancé
        Mockito.when(etablissementService.getAllEtabEditeur(Mockito.any())).thenReturn(listeEtabs);

        Mockito.when(dateEnvoiEditeurRepository.findTopByOrderByDateEnvoiDesc()).thenReturn(Optional.empty());

        Calendar calendar = Calendar.getInstance();
        calendar.set(2020, 1, 1);
        Mockito.when(eventService.getLastDateModificationEtab(etabIn1)).thenReturn(calendar.getTime());
        calendar.set(2020, 11, 1);
        Mockito.when(eventService.getLastDateModificationEtab(etabIn2)).thenReturn(calendar.getTime());

        List<Integer> listIds = new ArrayList<>();
        listIds.add(2);
        listIds.add(3);
        List<ExportEtablissementEditeurDto> result = service.getItems(listIds);

        Assertions.assertEquals(0, result.size());
    }*/
}
