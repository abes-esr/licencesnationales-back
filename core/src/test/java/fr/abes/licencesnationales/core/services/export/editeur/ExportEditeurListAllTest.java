package fr.abes.licencesnationales.core.services.export.editeur;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.abes.licencesnationales.core.constant.Constant;
import fr.abes.licencesnationales.core.converter.ExportConverter;
import fr.abes.licencesnationales.core.converter.UtilsMapper;
import fr.abes.licencesnationales.core.entities.TypeEtablissementEntity;
import fr.abes.licencesnationales.core.entities.etablissement.ContactEntity;
import fr.abes.licencesnationales.core.entities.etablissement.EtablissementEntity;
import fr.abes.licencesnationales.core.entities.ip.IpEntity;
import fr.abes.licencesnationales.core.entities.ip.IpV4;
import fr.abes.licencesnationales.core.entities.statut.StatutIpEntity;
import fr.abes.licencesnationales.core.exception.IpException;
import fr.abes.licencesnationales.core.services.EtablissementService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ExportEditeurListAll.class, ObjectMapper.class, ExportConverter.class, UtilsMapper.class})
public class ExportEditeurListAllTest {
    @Autowired
    private ExportEditeurListAll service;

    @Autowired
    private UtilsMapper mapper;

    @MockBean
    private EtablissementService etablissementService;

    @DisplayName("test génération csv listAll")
    @Test
    void testGenerateCsvListAll() throws IpException {
        StatutIpEntity statutValide = new StatutIpEntity(Constant.STATUT_IP_VALIDEE, "Validée");

        TypeEtablissementEntity type1 = new TypeEtablissementEntity(1, "testType");

        ContactEntity contact1 = new ContactEntity(1, "nom", "prenom", "adresse", "BP", "CP", "ville", "cedex", "telephone", "mail@mail.com", "password");

        //Etablissement avec 2 Ip validée et une Ip Nouvelle
        EtablissementEntity etabIn1 = new EtablissementEntity(2, "testNom2", "111111111", type1, "12345", contact1);

        IpEntity ip1 = new IpV4("4.4.4.4", "test", statutValide);
        IpEntity ip2 = new IpV4("5.5.5.5", "test", statutValide);
        etabIn1.ajouterIp(ip1);
        etabIn1.ajouterIp(ip2);

        //Etablissement avec une seule IP validée
        EtablissementEntity etabIn2 = new EtablissementEntity(3, "testNom3", "222222222", type1, "654321", contact1);

        IpEntity ip3 = new IpV4("6.6.6.6", "test", statutValide);
        etabIn2.ajouterIp(ip3);

        List<EtablissementEntity> listeEtabs = new ArrayList<>();
        listeEtabs.add(etabIn1);
        listeEtabs.add(etabIn2);

        Mockito.when(etablissementService.getAllEtabEditeur(Mockito.any())).thenReturn(listeEtabs);

        List<Integer> listeType = new ArrayList<>();
        listeType.add(1);
        ByteArrayInputStream csv = service.generateCsv(listeType);

        int n = csv.available();
        byte[] bytes = new byte[n];
        csv.read(bytes, 0, n);
        String csvString = new String(bytes, StandardCharsets.UTF_8);

        StringBuilder result = new StringBuilder("ID Etablissement;Nom Etablissement;Type d'Etablissement;Adresse;Boite Postale;Code Postal;Cedex;Ville;Contact Nom;Contact Email;Contact Tel;Liste Acces\r\n");
        result.append("12345;testNom2;testType;adresse;BP;CP;cedex;ville;nom prenom;mail@mail.com;telephone;4.4.4.4;5.5.5.5\r\n");
        result.append("654321;testNom3;testType;adresse;BP;CP;cedex;ville;nom prenom;mail@mail.com;telephone;6.6.6.6\r\n");

        Assertions.assertEquals(result.toString(), csvString);
    }
}
